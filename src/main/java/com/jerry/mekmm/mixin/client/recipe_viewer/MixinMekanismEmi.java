// package com.jerry.mekmm.mixin.client.recipe_viewer;
//
// import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
// import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
//
// import com.jerry.mekmm.common.util.MoreMachineUtils;
//
// import mekanism.client.recipe_viewer.emi.MekanismEmi;
// import mekanism.common.block.attribute.Attribute;
// import mekanism.common.block.attribute.AttributeFactoryType;
// import mekanism.common.registries.MekanismBlocks;
// import mekanism.common.tier.FactoryTier;
// import mekanism.common.util.EnumUtils;
//
// import net.minecraft.world.item.BlockItem;
// import net.minecraft.world.item.Item;
// import net.minecraft.world.level.ItemLike;
//
// import dev.emi.emi.api.EmiPlugin;
// import dev.emi.emi.api.EmiRegistry;
// import dev.emi.emi.api.recipe.EmiRecipeCategory;
// import dev.emi.emi.api.stack.EmiStack;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Overwrite;
//
// import java.util.List;
//
// @Mixin(value = MekanismEmi.class, remap = false)
// public abstract class MixinMekanismEmi implements EmiPlugin {
//
// /**
// * @author LostMyself
// * @reason 不得已这么做，有冲突立刻删除
// */
// @Overwrite
// private static void addWorkstations(EmiRegistry registry, EmiRecipeCategory category, List<ItemLike> workstations) {
// for (ItemLike workstation : workstations) {
// Item item = workstation.asItem();
// registry.addWorkstation(category, EmiStack.of(item));
// if (item instanceof BlockItem blockItem) {
// AttributeAdvancedFactoryType advancedFactoryType = Attribute.get(blockItem.getBlock(),
// AttributeAdvancedFactoryType.class);
// if (advancedFactoryType != null) {
// for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
// registry.addWorkstation(category, EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier,
// advancedFactoryType.getAdvancedFactoryType())));
// }
// }
// AttributeFactoryType factoryType = Attribute.get(blockItem.getBlock(), AttributeFactoryType.class);
// if (factoryType != null) {
// for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
// registry.addWorkstation(category, EmiStack.of(MekanismBlocks.getFactory(tier, factoryType.getFactoryType())));
// }
// }
// }
// }
// }
// }
