package com.gantenx.raffles.config;

public enum Category {
    CALCULATE(1, "calculate", "计算类");

    private final int id;
    private final String name;
    private final String description;

    Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Category getCategory(int id) {
        for (Category category : Category.values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown Category id: " + id);
    }
}
