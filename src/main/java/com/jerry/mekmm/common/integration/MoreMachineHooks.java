package com.jerry.mekmm.common.integration;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class MoreMachineHooks {

    public record IntegrationInfo(String modId, boolean isLoaded) {

        private IntegrationInfo(String modId, Predicate<String> loadedCheck) {
            this(modId, loadedCheck.test(modId));
        }

        private void sendImc(String method, Supplier<?> toSend) {
            InterModComms.sendTo(modId, method, toSend);
        }

        public ResourceLocation rl(String path) {
            return ResourceLocation.fromNamespaceAndPath(modId, path);
        }

        public void assertLoaded() {
            if (!isLoaded) {
                throw new IllegalStateException(modId + " is not loaded");
            }
        }
    }

    public final IntegrationInfo evolvedMekanism;
    public final IntegrationInfo mekanismgenerators;

    public MoreMachineHooks() {
        ModList modList = ModList.get();
        // Note: The modList is null when running tests
        Predicate<String> loadedCheck = modList == null ? modId -> false : modList::isLoaded;
        evolvedMekanism = new IntegrationInfo("evolvedmekanism", loadedCheck);
        mekanismgenerators = new IntegrationInfo("mekanismgenerators", loadedCheck);
    }
}
