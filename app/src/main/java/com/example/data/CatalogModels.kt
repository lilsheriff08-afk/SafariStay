package com.example.data

data class ItineraryItem(
    val day: Int,
    val activity: String,
    val description: String
)

data class StayItem(
    val id: String,
    val title: String,
    val location: String,
    val country: String,
    val description: String,
    val pricePerNight: Double,
    val rating: Double,
    val imageResName: String,
    val imageUrls: List<String>,
    val amenities: List<String>,
    val reviewsCount: Int
)

data class SafariItem(
    val id: String,
    val title: String,
    val park: String,
    val country: String,
    val durationDays: Int,
    val price: Double,
    val rating: Double,
    val imageResName: String,
    val imageUrls: List<String>,
    val inclusions: List<String>,
    val description: String
)

data class ExperienceItem(
    val id: String,
    val title: String,
    val location: String,
    val durationHours: Double,
    val price: Double,
    val rating: Double,
    val imageResName: String,
    val description: String
)

data class EventItem(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val region: String,
    val description: String,
    val imageUrl: String
)

object SafariCatalog {
    val events = listOf(
        EventItem(
            id = "event_migration",
            title = "Wildebeest Migration Peak",
            date = "August 15, 2026",
            location = "Maasai Mara, Kenya",
            region = "Kenya",
            description = "Witness the breathtaking peak of the Great Migration as millions of wildebeest cross the Mara River.",
            imageUrl = "https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=800&q=80"
        ),
        EventItem(
            id = "event_sauti_za_busara",
            title = "Sauti za Busara Festival",
            date = "February 12, 2027",
            location = "Stone Town, Zanzibar",
            region = "Tanzania",
            description = "Experience the vibrant sounds of Africa at one of the continent's largest and most respected music festivals.",
            imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80"
        ),
        EventItem(
            id = "event_kili_marathon",
            title = "Kilimanjaro Marathon",
            date = "February 28, 2027",
            location = "Moshi, Tanzania",
            region = "Tanzania",
            description = "Run the scenic routes at the foothills of Mount Kilimanjaro in this internationally recognized marathon.",
            imageUrl = "https://images.unsplash.com/photo-1552674605-db6ffd4facb5?auto=format&fit=crop&w=800&q=80"
        ),
        EventItem(
            id = "event_blankets_wine",
            title = "Blankets & Wine",
            date = "October 4, 2026",
            location = "Nairobi, Kenya",
            region = "Kenya",
            description = "A premier East African music experience, featuring live Afro-indie music, food, and craft stalls.",
            imageUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?auto=format&fit=crop&w=800&q=80"
        ),
        EventItem(
            id = "event_nyama_choma",
            title = "Nairobi Nyama Choma Festival",
            date = "November 21, 2026",
            location = "Nairobi, Kenya",
            region = "Kenya",
            description = "Celebrate East Africa's BBQ culture with the best grilled meats, local music, and cultural performances.",
            imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&w=800&q=80"
        ),
        EventItem(
            id = "event_zanzibar_film",
            title = "Zanzibar International Film Festival",
            date = "July 1, 2026",
            location = "Stone Town, Zanzibar",
            region = "Tanzania",
            description = "The largest multi-disciplinary art and cultural festival in East Africa, focusing on Dhow countries.",
            imageUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80"
        )
    )

    val stays = listOf(
        StayItem(
            id = "stay_mara_lodge",
            title = "Savanna Horizon Eco-Lodge",
            location = "Maasai Mara National Reserve",
            country = "Kenya",
            description = "Experience uncompromised comfort on elevated wood-deck suites situated directly along the wildebeest migration corridor. Wake up to panoramic views of herds and enjoy high-end dining under the starry savanna sky.",
            pricePerNight = 350.0,
            rating = 4.92,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Panoramic Sun Deck", "Eco-Friendly Solar Power", "Savanna Infinity Pool", "Guided Night Drives"),
            reviewsCount = 142
        ),
        StayItem(
            id = "stay_serengeti_tents",
            title = "Serengeti Vista Tented Camp",
            location = "Central Serengeti plains",
            country = "Tanzania",
            description = "Immerse yourself in authentic wild Africa. These ultra-luxury canvas suites combine heritage colonial safari aesthetic with modern copper baths, open fireplaces, and private dining decks over the plains.",
            pricePerNight = 450.0,
            rating = 4.88,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1527066577390-7c154215915d?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Private En-Suite Bath", "Outdoor Bonfire Lounge", "24/7 Security Patrol", "Bush Spa Access"),
            reviewsCount = 96
        ),
        StayItem(
            id = "stay_zanzibar_pavilion",
            title = "Zanzibar Beachfront Swahili Pavilion",
            location = "Nungwi Beach, Zanzibar Archipelago",
            country = "Tanzania",
            description = "Where Swahili heritage meets beach luxury. Carved teak wood screens, whitewashed walls, and coral stones make up this private beachfront villa with stepping stones straight onto white powder sands.",
            pricePerNight = 180.0,
            rating = 4.95,
            imageResName = "img_luxury_lodge", // fall back gracefully to a beautiful lodge
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1506929562872-bb421d03ef2a?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Private Sand Beach", "Infinity Plunge Pool", "Traditional Spice Bar", "Scuba Diving Gear"),
            reviewsCount = 204
        ),
        StayItem(
            id = "stay_ngorongoro_suite",
            title = "Crater-Rim Ridge Sanctuary",
            location = "Ngorongoro Conservation Area",
            country = "Tanzania",
            description = "Perched 2,200 meters above sea level on the edge of the ancient volcanic crater. Features floor-to-ceiling glass walls, warm stone fireplaces, and binoculars in every room to spot black rhinos from your bed.",
            pricePerNight = 480.0,
            rating = 4.79,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Telescopic Crater View", "Stone Fireplace", "Personal Chef Service", "Helipad Access"),
            reviewsCount = 68
        )
    )

    val safaris = listOf(
        SafariItem(
            id = "safari_great_migration",
            title = "Great Migration Expedition",
            park = "Serengeti & Maasai Mara",
            country = "Kenya & Tanzania",
            durationDays = 6,
            price = 1200.0,
            rating = 4.97,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=800&q=80"
            ),
            inclusions = listOf("All Luxury Lodging", "4x4 Safari Land Cruiser", "Professional Naturalist Guide", "National Park Fees"),
            description = "The ultimate African savanna odyssey. Follow the millions of wildebeest, zebras, and gazelles as they brave river crossings and predators across the transboundary plains of East Africa."
        ),
        SafariItem(
            id = "safari_crater_wild",
            title = "Ngorongoro & Tarangire Escape",
            park = "Ngorongoro Volcanic Caldera",
            country = "Tanzania",
            durationDays = 4,
            price = 850.0,
            rating = 4.85,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1547721064-9c2934b4b35e?auto=format&fit=crop&w=800&q=80"
            ),
            inclusions = listOf("Rim-side Tented Camp", "Baobab Walking Tour", "Full-Day Caldera Game Drive", "Local Swahili Meals"),
            description = "Explore the largest unbroken caldera in the world, home to over 30,000 animals including Africa's densest concentration of lions, elephants, and rare black rhinos."
        ),
        SafariItem(
            id = "safari_zanzibar_sea",
            title = "Zanzibar Spice & Sail Tour",
            park = "Stone Town & Indian Ocean",
            country = "Tanzania",
            durationDays = 3,
            price = 300.0,
            rating = 4.90,
            imageResName = "img_safari_balloon", // utilizing beautiful balloon sunrise sea look
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1506929562872-bb421d03ef2a?auto=format&fit=crop&w=800&q=80"
            ),
            inclusions = listOf("Stone Town Boutique Hotel", "Private Dhow Sailing Boat", "Organic Spice Farm Tour", "Marine Sanctuary Snorkeling"),
            description = "Immerse yourself in history, aroma, and ocean waves. Tour historic Stone Town, sail to pristine sandbanks on a traditional wooden boat, and swim with sea turtles."
        )
    )

    val experiences = listOf(
        ExperienceItem(
            id = "exp_mara_balloon",
            title = "Serengeti Sunrise Hot Air Balloon",
            location = "Serengeti Plains, Tanzania",
            durationHours = 3.5,
            price = 450.0,
            rating = 4.99,
            imageResName = "img_safari_balloon",
            description = "Ascend silently into the cold savanna morning. Watch the golden sunrise ignite the acacia tops and view massive herds of elephants and wildebeest moving below. Concludes with a grand champagne bush breakfast."
        ),
        ExperienceItem(
            id = "exp_maasai_culture",
            title = "Authentic Maasai Cultural Journey",
            location = "Mara Manyatta, Kenya",
            durationHours = 2.0,
            price = 60.0,
            rating = 4.88,
            imageResName = "img_luxury_lodge",
            description = "Spend a half-day welcomed into a traditional Manyatta village. Learn ancient fire-starting techniques, participate in the Adumu jumping dance, see beautiful beaded beadwork, and support local community schools."
        ),
        ExperienceItem(
            id = "exp_zanzibar_dhow",
            title = "Sunset Swahili Dhow Cruise & Feast",
            location = "Nungwi Swahili Coast, Zanzibar",
            durationHours = 4.0,
            price = 95.0,
            rating = 4.91,
            imageResName = "img_safari_hero",
            description = "Set sail on a handcrafted mahogany dhow as the equatorial sun dips below the turquoise horizon. Enjoy traditional Taarab live music on board and an organic wood-grilled rock lobster beach dinner."
        )
    )
}
