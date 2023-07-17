package com.nate.elemental.utils.shops.spawner;

public class Spawner {
	
    public String getFormattedEntityName(String entityName) {
        entityName = entityName.substring(0, 1).toUpperCase() + entityName.substring(1).toLowerCase();
        entityName = entityName.replace("_", " ");
        return entityName;
    }
}
