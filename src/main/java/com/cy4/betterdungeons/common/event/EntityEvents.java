package com.cy4.betterdungeons.common.event;

import java.util.Random;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.block.DungeonCrateBlock;
import com.cy4.betterdungeons.common.world.spawner.EntityScaler;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.network.data.DungeonRunData;
import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity().world.isRemote || !(event.getEntity() instanceof MonsterEntity)
				|| event.getEntity().world.getDimensionKey() != DimensionInit.DUNGEON_WORLD
				|| event.getEntity().getTags().contains("DungeonScaled"))
			return;

		MonsterEntity entity = (MonsterEntity) event.getEntity();
		DungeonRun raid = DungeonRunData.get((ServerWorld) entity.world).getAt(entity.getPosition());
		if (raid == null)
			return;

		EntityScaler.scaleDungeon(entity, raid.level, new Random(), EntityScaler.Type.MOB);
		entity.getTags().add("DungeonScaled");
		entity.enablePersistence();
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity().world.isRemote || event.getEntity().world.getDimensionKey() != DimensionInit.DUNGEON_WORLD
				|| !event.getEntity().getTags().contains("DungeonBoss"))
			return;

		ServerWorld world = (ServerWorld) event.getEntityLiving().world;
		DungeonRun raid = DungeonRunData.get(world).getAt(event.getEntity().getPosition());

		if (raid != null) {
			raid.runIfPresent(world.getServer(), player -> {
				LootContext.Builder builder = (new LootContext.Builder(world)).withRandom(world.rand)
						.withParameter(LootParameters.THIS_ENTITY, player)
						.withParameter(LootParameters.field_237457_g_, event.getEntity().getPositionVec())
						.withParameter(LootParameters.DAMAGE_SOURCE, event.getSource())
						.withNullableParameter(LootParameters.KILLER_ENTITY, event.getSource().getTrueSource())
						.withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, event.getSource().getImmediateSource())
						.withParameter(LootParameters.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());

				LootContext ctx = builder.build(LootParameterSets.ENTITY);

				NonNullList<ItemStack> stacks = NonNullList.create();
				stacks.addAll(world.getServer().getLootTableManager()
						.getLootTableFromLocation(new ResourceLocation(BetterDungeons.MOD_ID, "chest/boss")).generate(ctx));
				ItemStack crate = DungeonCrateBlock.getCrateWithLoot((DungeonCrateBlock) BlockInit.DUNGEON_CRATE.get(), stacks);

				event.getEntity().entityDropItem(crate);

				FireworkRocketEntity fireworks = new FireworkRocketEntity(world, event.getEntity().getPosX(), event.getEntity().getPosY(),
						event.getEntity().getPosZ(), new ItemStack(Items.FIREWORK_ROCKET));
				world.addEntity(fireworks);

				raid.won = true;
				raid.ticksLeft = 20 * 20;
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
						SoundCategory.MASTER, 1.0F, 1.0F);

				StringTextComponent title = new StringTextComponent("Dungeon Cleared!");
				title.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

				Entity entity = event.getEntity();

				IFormattableTextComponent entityName = entity.getType().getName().deepCopy();
				entityName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_dd711e)));
				IFormattableTextComponent subtitle = new StringTextComponent(" is defeated.");
				subtitle.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

				StringTextComponent actionBar = new StringTextComponent("You'll be teleported back soon...");
				actionBar.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ddd01e)));

				STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE, title);
				STitlePacket subtitlePacket = new STitlePacket(STitlePacket.Type.SUBTITLE, entityName.deepCopy().append(subtitle));

				player.connection.sendPacket(titlePacket);
				player.connection.sendPacket(subtitlePacket);
				player.sendStatusMessage(actionBar, true);

				IFormattableTextComponent playerName = player.getDisplayName().deepCopy();
				playerName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_983198)));

				StringTextComponent text = new StringTextComponent(" cleared a Dungeon by defeating ");
				text.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ffffff)));

				StringTextComponent punctuation = new StringTextComponent("!");
				punctuation.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_ffffff)));

				world.getServer().getPlayerList().func_232641_a_(playerName.append(text).append(entityName).append(punctuation),
						ChatType.CHAT, player.getUniqueID());
			});
		}
	}

	@SubscribeEvent
	public static void onEntityDrops(LivingDropsEvent event) {
		if (event.getEntity().world.isRemote)
			return;
		if (event.getEntity().world.getDimensionKey() != DimensionInit.DUNGEON_WORLD)
			return;
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
		if (event.getEntity().getEntityWorld().getDimensionKey() == DimensionInit.DUNGEON_WORLD && !event.isSpawner()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerHurt(LivingHurtEvent event) {
		if (event.getEntity() instanceof PlayerEntity && !event.getEntity().world.isRemote) {
			DungeonRun raid = DungeonRunData.get((ServerWorld) event.getEntity().world).getAt(event.getEntity().getPosition());

			if (raid != null && raid.won) {
				event.setCanceled(true);
			}
		}
	}
}