package com.froobworld.archiveroute.data;

import com.froobworld.archiveroute.ArchiveRoute;
import com.velocitypowered.api.proxy.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ExileManager {
    private final ExileDataManager exileDataManager;
    private final Path dataFilePath;
    private final Set<ExileEntry> exileEntries = new HashSet<>();
    private final List<ExileEquivalenceClass> equivalenceClasses = new ArrayList<>();

    public ExileManager(ArchiveRoute archiveRoute) {
        exileDataManager = new ExileDataManager();
        dataFilePath = new File(archiveRoute.getDataDirectory().toString(), "exile_data.json").toPath();
        for(ExileEntry exileEntry : exileDataManager.loadObjectsFromFile(dataFilePath)) {
            addExile(exileEntry.uuid, exileEntry.ipAddress);
        }
        archiveRoute.getServer().getEventManager().register(archiveRoute, new ExileEnforcer(archiveRoute, this));
    }

    public boolean checkExile(Player player) {
        UUID uuid = player.getUniqueId();
        String ipAddress = player.getRemoteAddress().getAddress().toString().replace("/", "");
        ExileEntry exileEntry = new ExileEntry(uuid, ipAddress);
        for (ExileEquivalenceClass equivalenceClass : equivalenceClasses) {
            if (equivalenceClass.isEquivalent(exileEntry)) {
                if (!exileEntries.contains(exileEntry)) {
                    addExile(uuid, ipAddress);
                }
                return true;
            }
        }
        return false;
    }

    public void addExile(UUID uuid, String ipAddress) {
        ExileEntry exileEntry = new ExileEntry(uuid, ipAddress);
        exileEntries.add(exileEntry);
        ExileEquivalenceClass equivalenceClass = new ExileEquivalenceClass();
        equivalenceClass.add(exileEntry);

        ListIterator<ExileEquivalenceClass> iterator = equivalenceClasses.listIterator();
        while (iterator.hasNext()) {
            ExileEquivalenceClass otherEquivalenceClass = iterator.next();
            if (otherEquivalenceClass.isEquivalent(exileEntry)) {
                equivalenceClass.addAll(otherEquivalenceClass);
                iterator.remove();
            }
        }
        equivalenceClasses.add(equivalenceClass);
        exileDataManager.saveObjectsToFile(exileEntries, dataFilePath);
    }

    record ExileEntry(UUID uuid, String ipAddress) {
        public boolean matches(UUID uuid, String ipAddress) {
            return this.uuid.equals(uuid) || this.ipAddress.equalsIgnoreCase(ipAddress);
        }
    }

    private static class ExileEquivalenceClass {
        private final Set<ExileEntry> exileEntries = new HashSet<>();

        public ExileEquivalenceClass() {
        }

        public boolean isEquivalent(ExileEntry exileEntry) {
            for (ExileEntry containedExileEntries : exileEntries) {
                if (containedExileEntries.matches(exileEntry.uuid, exileEntry.ipAddress)) {
                    return true;
                }
            }
            return false;
        }

        public void add(ExileEntry exileEntry) {
            exileEntries.add(exileEntry);
        }

        public void addAll(ExileEquivalenceClass exileEquivalenceClass) {
            exileEquivalenceClass.exileEntries.forEach(this::add);
        }

    }

}
