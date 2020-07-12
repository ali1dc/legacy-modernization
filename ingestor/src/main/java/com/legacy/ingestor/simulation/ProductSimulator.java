package com.legacy.ingestor.simulation;

import com.legacy.ingestor.model.Category;
import com.legacy.ingestor.model.LegacyProduct;
import com.legacy.ingestor.repository.CategoryRepository;
import com.legacy.ingestor.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductSimulator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String createdBy = "random-generator";
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private final List<String> categories = Arrays.asList("Electronics", "Arts", "Beverages", "Goods", "Clothing",
            "Computers", "Books", "Cleaning Supplies", "Tablets", "Small Appliances", "Appliances", "Tools",
            "Small Furniture", "Furniture", "Healthcare", "Hardware", "Audio Equipments", "Computer Accessories",
            "Laptops and Notebooks", "Cameras and Camera Equipment");

    private final List<String> tools = Arrays.asList("DEWALT 20 oz. Hammer", "Husky - Torque Wrench", "DEWALT Adjustable Wrench",
            "Husky 1/4 in. Drive Torque Wrench", "Husky 10 in. Heavy-Duty Pipe Wrench", "Husky 12 in. Double Speed Adjustable Wrench",
            "Milwaukee 14 in. Steel Pipe Wrench", "Bosch BLAZE Laser Distance Measurer");
    private final List<String> electronics = Arrays.asList("Google Chromecast", "LG ThinQ Smart Speaker",
            "Sound Mates Wireless Stereo Earbuds", "Roku Streaming Stick Plus Streaming Player", "Google Nest Hub Chalk",
            "Google Home Mini Chalk", "iLive Portable Wireless Speaker");
    private final List<String> laptops = Arrays.asList("Apple - MacBook Air 13.3 inch", "Apple - MacBook Pro - 16", "Apple - MacBook Pro - 13",
            "Acer Chromebook Spin", "HP - Pavilion x360 2-in-1 14 inch", "HP - 15.6 Touch-Screen Laptop", "ASUS - 11.6\" Chromebook",
            "HP - 14\" Chromebook", "HP - 14\" Laptop", "Dell - Inspiron 15.6\" Touch-Screen Laptop",
            "HP - Spectre x360 2-in-1 15.6\" 4K Ultra HD Touch-Screen Laptop");
    private final List<String> tablets = Arrays.asList("Apple - 11-Inch iPad Pro", "Apple - iPad (Latest Model) with Wi-Fi - 32GB",
            "Apple - 12.9-Inch iPad Pro", "Microsoft - Surface Pro 7", "Digiland - 10.1\" - Tablet - 32GB", "Samsung - Galaxy Tab S6 Lite",
            "Samsung - Galaxy Tab A Kids Edition", "Samsung - Galaxy Tab A (2019)", "Amazon - Fire HD 10 Kids Edition", "Lenovo - Chromebook Duet",
            "Amazon - Kindle Oasis E-Reader (2019)", "Samsung - Galaxy Tab S5e", "Samsung - Galaxy Tab S6", "Microsoft - Surface Go 2");
    private final List<String> smallAppliances = Arrays.asList("Dash® Express Mini Chopper", "KitchenAid® 3.5-Cup Mini Food Chopper",
            "KitchenAid® 9-Cup Food Processor", "Oster® 1.1 cu. ft. Stainless Steel Microwave Oven", "Galanz Retro Style 0.7 cu. ft. Microwave Oven in Red",
            "Cuisinart® Convection Microwave Oven with Grill", "Magic Chef 1.6 cu. ft. Countertop Microwave Oven", "KitchenAid® K400 Blender",
            "CRUX® Artisan Series 7-Speed Blender", "Margaritaville® Bahamas™ Frozen Margarita Maker", "Breville® the 3X Bluicer™",
            "Calphalon® ActiveSense Blender", "Vitamix® 7500 Blender", "Breville® The Barista Express™ Espresso Machine", "Breville® Infuser™ Espresso Machine",
            "Breville™ Bambino™ Plus Stainless Steel Espresso Maker", "Breville® the Oracle Touch Complete Espresso Maker",
            "Calphalon® Temp iQ Espresso Machine with Grinder", "Breville® Barista Pro™ Stainless Steel Espresso Maker");
    private final List<String> smallFurnitureList = Arrays.asList("ORG 3-Cube Storage Bench", "Del Hutson Designs® Barb Console Table",
            "Forest Gate 52\" Charlotte Rustic Entry Console Table", "Relaxed Living Cube Organizer Collection", "Safavieh Samantha Console Table",
            "Umbra® Estique Wall Organizer in White", "Powell Antique Slimline Console in Blue/White", "SALT™ 3-Tier Wood Shoe Rack", "Modern Farmhouse Bed Tray",
            "Bee & Willow™ Home Cane Cabinet in White", "Bee & Willow™ Home Cane Upholstered Ottoman in Natural", "Global Caravan Round Pouf",
            "2-Tier Round Side Table", "Austin Furniture Collection", "Linon Home Whitley Furniture Collection", "Steve Silver Co. Lucia Table Collection");
    private final List<String> accessories = Arrays.asList("WD - Easystore 2TB External USB 3.0 Portable Hard Drive",
            "Logitech - H390 USB Headset with Noise-Canceling", "Apple - Magic Mouse 2 - Silver", "Samsung - 860 EVO 1TB Internal SATA Solid State Drive",
            "WD - Blue 500GB Internal SATA Solid State Drive", "Apple - USB-C-to-USB Adapter - White", "SanDisk - Ultra 256GB USB 3.0 Type A Flash Drive",
            "Apple - Magic Keyboard - Silver", "Insignia™ - 4-Port USB 3.0 Hub - Black", "Insignia™ - 6' DisplayPort-to-HDMI Cable - Black",
            "HyperX - Fury S Pro Gaming Mouse Pad", "Insignia™ - Mini DisplayPort-to-HDMI Adapter", "Insignia™ - USB Keyboard - Black",
            "Apple - 61W USB-C Power Adapter", "Blue Microphones - Yeti USB Multi-Pattern Electret Condenser", "Logitech - HD Webcam C270",
            "Logitech - 4K Pro Webcam", "Seagate - Expansion 2TB External USB 3.0 Portable Hard Drive");

//    @Bean
    public void categoryAndProductInsert() {
        // insert categories
        List<Category> cats = new ArrayList<>();
        categories.forEach(category -> {
            cats.add(Category.builder().name(category).createdBy(createdBy).build());
        });
        categoryRepository.saveAll(cats);
        // insert tools products
        Random random = new Random();
        productInsert("Tools", tools, 29.99F);
        productInsert("Electronics", electronics, 79.99F);
        productInsert("Tablets", tablets, 199.99F);
        productInsert("Small Appliances", smallAppliances, 179.99F);
        productInsert("Laptops and Notebooks", laptops, 699.99F);
        productInsert("Small Furniture", smallFurnitureList, 129.99F);
        productInsert("Computer Accessories", accessories, 59.99F);
    }

    private void productInsert(String categoryName, List<String> products, Float price) {
        Optional<Category> optionalCategory = categoryRepository.findTopByName(categoryName);
        optionalCategory.ifPresent(cat -> {
            List<LegacyProduct> productList = new ArrayList<>();
            products.forEach(prod -> {
                productList.add(LegacyProduct.builder()
                        .name(prod)
                        .category(cat)
                        .createdBy(createdBy)
                        .listPrice(price)
                        .quantity(100)
                        .build());
            });
            productRepository.saveAll(productList);
        });
    }
}
