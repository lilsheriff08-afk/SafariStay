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
    val reviewsCount: Int,
    val lat: Double? = null,
    val lng: Double? = null,
    val availabilityStatus: String = "Available", // "Available", "Limited", "Fully Booked"
    val hotelClass: String? = null, // e.g. "3-star", "4-star", "Luxury"
    val isSponsored: Boolean = false,
    val bookingSource: String? = null
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
    val description: String,
    val lat: Double? = null,
    val lng: Double? = null
)

data class ExperienceItem(
    val id: String,
    val title: String,
    val location: String,
    val durationHours: Double,
    val price: Double,
    val rating: Double,
    val imageResName: String,
    val description: String,
    val lat: Double? = null,
    val lng: Double? = null
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

data class SwahiliPhrase(
    val english: String,
    val swahili: String,
    val phonetic: String,
    val category: String
)

data class PackingItem(
    val name: String,
    val description: String,
    val category: String,
    val iconName: String,
    val requiredForEcosystems: List<String> = emptyList(), // e.g., "Savanna", "Rainforest", "Coastal"
    val requiredForActivities: List<String> = emptyList(), // e.g., "Game Drive", "Walking Safari", "Dhow Cruise"
    var isChecked: Boolean = false
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
            amenities = listOf("Pool", "Free Wi-Fi", "Free breakfast", "Spa"),
            reviewsCount = 142,
            lat = -1.406,
            lng = 35.215,
            availabilityStatus = "Available",
            hotelClass = "Luxury Lodge"
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
            amenities = listOf("Spa", "Free parking", "Breakfast included", "Free Wi-Fi"),
            reviewsCount = 96,
            lat = -2.333,
            lng = 34.833,
            availabilityStatus = "Limited",
            hotelClass = "Glamping Tent"
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
            amenities = listOf("Pool", "Free Wi-Fi", "Breakfast included", "Gym"),
            reviewsCount = 204,
            lat = -6.100,
            lng = 39.300,
            availabilityStatus = "Available",
            hotelClass = "Beach Resort"
        ),
        StayItem(
            id = "stay_uganda_boutique",
            title = "Le Petit Village Hotel & Spa",
            location = "Ggaba Road, Kampala",
            country = "Uganda",
            description = "An oasis of tranquility in the heart of Kampala. This luxury boutique hotel offers authentic thatched-roof cottages, Mediterranean-style courtyards, and a serene pool, providing a perfect start or end to your Ugandan safari adventure.",
            pricePerNight = 195.0,
            rating = 4.85,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Pool", "Free parking", "Free Wi-Fi"),
            reviewsCount = 186,
            lat = 0.299,
            lng = 32.596,
            availabilityStatus = "Fully Booked",
            hotelClass = "Boutique Hotel"
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
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Gym"),
            reviewsCount = 68,
            lat = -3.200,
            lng = 35.500,
            availabilityStatus = "Available",
            hotelClass = "Luxury Lodge"
        ),
        StayItem(
            id = "stay_merica_nakuru",
            title = "Merica Hotel Nakuru",
            location = "Nakuru Town",
            country = "Kenya",
            description = "Relaxed hotel with dining & a pool. Perfectly situated for exploring Lake Nakuru National Park.",
            pricePerNight = 70.45,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 3035,
            lat = 0.283,
            lng = 36.067,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_alba_meru",
            title = "Alba Hotel Meru",
            location = "Meru Town",
            country = "Kenya",
            description = "Polished hotel with dining & a pool. A sophisticated choice for travelers in the Meru region.",
            pricePerNight = 84.6,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 2044,
            lat = 0.050,
            lng = 37.650,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_mwingi_cottage",
            title = "Mwingi Cottage Hotel",
            location = "Mwingi",
            country = "Kenya",
            description = "Cozy 3-star hotel with essential amenities for a relaxed stay.",
            pricePerNight = 45.0,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1527066577390-7c154215915d?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Wi-Fi"),
            reviewsCount = 127,
            lat = -0.933,
            lng = 38.067,
            availabilityStatus = "Limited",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_sunrise_place",
            title = "Sunrise Place Executive Accommodation",
            location = "Makutano, Meru",
            country = "Kenya",
            description = "Modern executive accommodation offering professional service and comfortable rooms.",
            pricePerNight = 55.0,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1506929562872-bb421d03ef2a?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Executive Suites", "Free Parking", "Meeting Facilities", "Wi-Fi Access"),
            reviewsCount = 104,
            lat = 0.052,
            lng = 37.652,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_meru_slopes",
            title = "Meru Slopes Hotel",
            location = "Meru",
            country = "Kenya",
            description = "Casual hotel with dining, a spa & a pool.",
            pricePerNight = 74.26,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 1879,
            lat = 0.048,
            lng = 37.648,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_distant_relatives_kilifi",
            title = "Distant Relatives Ecolodge & Backpackers",
            location = "Kilifi",
            country = "Kenya",
            description = "Hip hostel with dining & beach access",
            pricePerNight = 25.5,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 1078,
            lat = -3.63,
            lng = 39.85,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_baobab_sea_lodge_kilifi",
            title = "Baobab Sea Lodge-Kilifi",
            location = "Kilifi",
            country = "Kenya",
            description = "Relaxed lodging with ocean views and modern amenities.",
            pricePerNight = 119.3,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 529,
            lat = -3.64,
            lng = 39.86,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_saltys_kitesurf_kilifi",
            title = "Salty's KiteSurf Village, Beach Bar and Restaurant",
            location = "Kilifi",
            country = "Kenya",
            description = "Unassuming guesthouse with dining",
            pricePerNight = 49.7,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 498,
            lat = -3.62,
            lng = 39.84,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_kilifi_bay_beach",
            title = "Kilifi Bay Beach Resort",
            location = "Kilifi",
            country = "Kenya",
            description = "An exquisite resort located on the sandy beaches of Kilifi.",
            pricePerNight = 163.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 820,
            lat = -3.635,
            lng = 39.855,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_mnarani_beach_club",
            title = "Mnarani Beach Club",
            location = "Kilifi",
            country = "Kenya",
            description = "Relaxed beachfront hotel with a spa",
            pricePerNight = 189.0,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 2015,
            lat = -3.645,
            lng = 39.865,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_bofa_beach_resort",
            title = "Bofa Beach Resort",
            location = "Kilifi",
            country = "Kenya",
            description = "A serene beach resort on the white sands of Bofa Beach.",
            pricePerNight = 82.3, // 10.7K
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 703,
            lat = -3.61,
            lng = 39.85,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_peacock_hotel_kilifi",
            title = "Peacock Hotel Kilifi",
            location = "Kilifi",
            country = "Kenya",
            description = "Comfortable 3-star hotel in Kilifi.",
            pricePerNight = 18.9, // 2,456
            rating = 3.9,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Free parking"),
            reviewsCount = 136,
            lat = -3.63,
            lng = 39.85,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_villa_carino_kilifi",
            title = "Villa Carino Kilifi",
            location = "Kilifi",
            country = "Kenya",
            description = "Bed & breakfast with excellent service and views.",
            pricePerNight = 50.0, // 6,501
            rating = 5.0,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Breakfast"),
            reviewsCount = 5,
            lat = -3.63,
            lng = 39.85,
            availabilityStatus = "Available",
            hotelClass = "Bed & breakfast"
        ),
        StayItem(
            id = "stay_lamu_house_hotel",
            title = "Lamu House Hotel",
            location = "Lamu",
            country = "Kenya",
            description = "3-star hotel with great amenities.",
            pricePerNight = 159.3,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 74,
            lat = -2.269,
            lng = 40.900,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_pwani_house_lamu",
            title = "Pwani House - Lamu Seafront",
            location = "Lamu",
            country = "Kenya",
            description = "Guest house by the sea front.",
            pricePerNight = 47.5,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Sea view"),
            reviewsCount = 25,
            lat = -2.270,
            lng = 40.902,
            availabilityStatus = "Available",
            hotelClass = "Guest house"
        ),
        StayItem(
            id = "stay_jambo_house_lamu",
            title = "JamboHouse Lamu",
            location = "Lamu",
            country = "Kenya",
            description = "Comfortable lodging with airport shuttle.",
            pricePerNight = 24.8,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Airport shuttle"),
            reviewsCount = 106,
            lat = -2.268,
            lng = 40.901,
            availabilityStatus = "Available",
            hotelClass = "Lodging"
        ),
        StayItem(
            id = "stay_subira_house",
            title = "Subira House Hotel and Restaurant",
            location = "Lamu",
            country = "Kenya",
            description = "Refined hotel with a terrace & sea views.",
            pricePerNight = 41.7,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Airport shuttle", "Restaurant"),
            reviewsCount = 82,
            lat = -2.271,
            lng = 40.903,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_the_majlis_hotel",
            title = "The Majlis Hotel Lamu",
            location = "Lamu",
            country = "Kenya",
            description = "Exquisite hotel resort.",
            pricePerNight = 384.6,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi", "Beach access"),
            reviewsCount = 679,
            lat = -2.265,
            lng = 40.910,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_peponi_hotel",
            title = "Peponi Hotel Lamu - Kenya",
            location = "Lamu",
            country = "Kenya",
            description = "Casual seasonal lodging with ocean views.",
            pricePerNight = 250.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 725,
            lat = -2.260,
            lng = 40.912,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_silver_rock_malindi",
            title = "Silver Rock Boutique Resort by Mokawa",
            location = "Malindi",
            country = "Kenya",
            description = "Casual quarters, plus dining & a pool",
            pricePerNight = 42.3,
            rating = 3.9,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Beach access"),
            reviewsCount = 350,
            lat = -3.220,
            lng = 40.125,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_q_boutique_malindi",
            title = "Q BOUTIQUE RESORT MALINDI",
            location = "Malindi",
            country = "Kenya",
            description = "Boutique resort with excellent facilities.",
            pricePerNight = 154.6,
            rating = 4.9,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 100,
            lat = -3.225,
            lng = 40.128,
            availabilityStatus = "Available",
            hotelClass = "Resort",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_ocean_beach_resort_malindi",
            title = "Ocean Beach Resort & Spa Malindi",
            location = "Malindi",
            country = "Kenya",
            description = "Upmarket beachfront resort with a spa.",
            pricePerNight = 316.1,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free Wi-Fi"),
            reviewsCount = 869,
            lat = -3.210,
            lng = 40.130,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_the_kasa_malindi",
            title = "The Kasa - Malindi ( Luxury Resort & Spa)",
            location = "Malindi",
            country = "Kenya",
            description = "Luxury resort and spa in Malindi.",
            pricePerNight = 250.7,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free breakfast"),
            reviewsCount = 406,
            lat = -3.215,
            lng = 40.135,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_billionaire_resort_malindi",
            title = "Billionaire Resort and Retreat",
            location = "Malindi",
            country = "Kenya",
            description = "Exclusive luxury retreat.",
            pricePerNight = 479.2,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free breakfast"),
            reviewsCount = 465,
            lat = -3.230,
            lng = 40.120,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_pine_breeze_mombasa",
            title = "Pine Breeze Holiday Resort",
            location = "Mombasa",
            country = "Kenya",
            description = "Casual quarters with pool and dining.",
            pricePerNight = 49.2,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Wi-Fi"),
            reviewsCount = 721,
            lat = -4.050,
            lng = 39.660,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_sarova_whitesands_mombasa",
            title = "Sarova Whitesands Beach Resort & Spa Mombasa - Beach Hotel in Mombasa",
            location = "Mombasa",
            country = "Kenya",
            description = "Luxe resort with 5 pools & a beach bar.",
            pricePerNight = 349.0,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 11101,
            lat = -4.000,
            lng = 39.730,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_swahili_social_house",
            title = "Swahili Social House",
            location = "Mombasa",
            country = "Kenya",
            description = "Relaxed guesthouse with a pool & dining.",
            pricePerNight = 20.5,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi", "Free parking", "Air-conditioned"),
            reviewsCount = 463,
            lat = -4.052,
            lng = 39.662,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_ocean_view_nyali",
            title = "Ocean View Nyali Boutique Hotel",
            location = "Mombasa",
            country = "Kenya",
            description = "Casual hotel with a restaurant & a pool.",
            pricePerNight = 24.0,
            rating = 3.8,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi", "Free parking", "Wi-Fi"),
            reviewsCount = 834,
            lat = -4.040,
            lng = 39.680,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_cityblue_creekside_mombasa",
            title = "CityBlue Creekside Hotel & Suites, Mombasa",
            location = "Mombasa",
            country = "Kenya",
            description = "Casual waterfront hotel with a pool.",
            pricePerNight = 55.4,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi", "Free parking", "Air-conditioned"),
            reviewsCount = 2549,
            lat = -4.045,
            lng = 39.665,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_mombasa_beach_hotel",
            title = "Mombasa Beach Hotel",
            location = "Mombasa",
            country = "Kenya",
            description = "Relaxed beachfront hotel with a pool.",
            pricePerNight = 61.5,
            rating = 4.0,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free breakfast"),
            reviewsCount = 2308,
            lat = -4.020,
            lng = 39.715,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_cowrie_shell_mombasa",
            title = "Cowrie Shell Beach Apartments Mombasa",
            location = "Mombasa",
            country = "Kenya",
            description = "Upmarket beach hotel with a pool & a bar.",
            pricePerNight = 50.3,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Free Wi-Fi", "Free parking"),
            reviewsCount = 1731,
            lat = -3.990,
            lng = 39.735,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_cocoa_boutique_mombasa",
            title = "Cocoa Boutique Hotel",
            location = "Mombasa",
            country = "Kenya",
            description = "Polished beachside hotel with a pool.",
            pricePerNight = 118.0,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free Wi-Fi"),
            reviewsCount = 461,
            lat = -3.995,
            lng = 39.730,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_ziwa_beach_resort",
            title = "Ziwa Beach Resort",
            location = "Mombasa",
            country = "Kenya",
            description = "Laid-back beachfront option with a bar.",
            pricePerNight = 91.0,
            rating = 3.9,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free Wi-Fi"),
            reviewsCount = 774,
            lat = -3.980,
            lng = 39.740,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_prideinn_mombasa_city",
            title = "PrideInn Hotel Mombasa City",
            location = "Mombasa",
            country = "Kenya",
            description = "Unfussy hotel with an eatery & a spa.",
            pricePerNight = 102.4,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 1381,
            lat = -4.060,
            lng = 39.670,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_vidzo_royal_inn_mtwapa",
            title = "Vidzo Royal Inn Hotel ( Mtwapa)",
            location = "Mtwapa",
            country = "Kenya",
            description = "Hotel in Mtwapa.",
            pricePerNight = 37.1,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Free parking"),
            reviewsCount = 32,
            lat = -3.950,
            lng = 39.750,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_tulia_backpackers_nyali",
            title = "Tulia Backpackers Nyali",
            location = "Mombasa",
            country = "Kenya",
            description = "Laid-back hostel with dining & a pool.",
            pricePerNight = 35.2,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi", "Air-conditioned", "Wi-Fi"),
            reviewsCount = 720,
            lat = -4.040,
            lng = 39.700,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_prideinn_paradise_mombasa",
            title = "PrideInn Paradise Beach Resort & Spa Mombasa",
            location = "Mombasa",
            country = "Kenya",
            description = "Beachfront resort with a water park.",
            pricePerNight = 298.8,
            rating = 4.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 17627,
            lat = -3.970,
            lng = 39.740,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_prideinn_flamingo_mombasa",
            title = "PrideInn Flamingo Beach Resort & Spa Mombasa",
            location = "Mombasa",
            country = "Kenya",
            description = "Casual beach resort with a pool & bars.",
            pricePerNight = 303.7,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 5227,
            lat = -3.980,
            lng = 39.735,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_sai_rock_beach_hotel",
            title = "Sai Rock Beach Hotel & Spa",
            location = "Mombasa",
            country = "Kenya",
            description = "Beach hotel and spa.",
            pricePerNight = 95.6,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Beach access", "Spa", "Free Wi-Fi"),
            reviewsCount = 1670,
            lat = -3.990,
            lng = 39.730,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_the_aiyana_beach_resort",
            title = "The Aiyana Beach Resort",
            location = "Pemba Island",
            country = "Tanzania",
            description = "Resort hotel.",
            pricePerNight = 1038.5,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 204,
            lat = -4.950,
            lng = 39.750,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_the_manta_resort",
            title = "The Manta Resort & Underwater Room | Pemba Island",
            location = "Pemba Island",
            country = "Tanzania",
            description = "Tranquil all-inclusive resort with a spa.",
            pricePerNight = 950.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 231,
            lat = -4.900,
            lng = 39.700,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_tulia_zanzibar_resort",
            title = "Tulia Zanzibar Unique Beach Resort",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Refined beachfront hotel with a pool.",
            pricePerNight = 884.6,
            rating = 4.8,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 304,
            lat = -6.130,
            lng = 39.350,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_zanzi_resort",
            title = "Zanzi Resort - Hotel Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Luxe resort with posh villas & bungalows.",
            pricePerNight = 354.6,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 321,
            lat = -6.100,
            lng = 39.200,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_villa_huruma",
            title = "Villa Huruma",
            location = "Zanzibar",
            country = "Tanzania",
            description = "3-star hotel.",
            pricePerNight = 154.0,
            rating = 4.8,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 144,
            lat = -6.160,
            lng = 39.200,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_sipano_beach_lodge",
            title = "Sipano Beach Lodge Kiwengwa",
            location = "Kiwengwa",
            country = "Tanzania",
            description = "Lodge in Kiwengwa.",
            pricePerNight = 50.4,
            rating = 3.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 140,
            lat = -5.990,
            lng = 39.370,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_be_zanzibar",
            title = "Be Zanzibar - Boutique Hotel",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Boutique hotel in Zanzibar.",
            pricePerNight = 432.0,
            rating = 4.8,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi"),
            reviewsCount = 332,
            lat = -6.150,
            lng = 39.205,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_hotel_riu_palace_zanzibar",
            title = "Hotel Riu Palace Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Lavish beachfront resort with a luxe spa.",
            pricePerNight = 550.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 2465,
            lat = -5.750,
            lng = 39.290,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_melia_zanzibar",
            title = "Meliá Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Chic beachfront resort with a spa.",
            pricePerNight = 672.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 1867,
            lat = -5.990,
            lng = 39.350,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_the_mora_zanzibar",
            title = "The Mora Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "5-star hotel in Zanzibar.",
            pricePerNight = 716.0,
            rating = 4.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 3138,
            lat = -6.000,
            lng = 39.340,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_paradise_beach_resort",
            title = "Paradise Beach Resort",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Laid-back beachfront property with pools.",
            pricePerNight = 213.0,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 1356,
            lat = -6.100,
            lng = 39.400,
        ),
        StayItem(
            id = "stay_majani_breeze_zanzibar",
            title = "Majani Breeze Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Beachfront hotel with free parking.",
            pricePerNight = 103.4,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Beach access"),
            reviewsCount = 16,
            lat = -6.100,
            lng = 39.200,
            availabilityStatus = "Available",
            hotelClass = "Hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_sunshine_azure",
            title = "Sunshine Azure Zanzibar Beach Hotel",
            location = "Zanzibar",
            country = "Tanzania",
            description = "4-star hotel in Zanzibar.",
            pricePerNight = 220.1,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 32,
            lat = -6.150,
            lng = 39.250,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_dream_of_zanzibar",
            title = "Dream Of Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "5-star beachfront hotel.",
            pricePerNight = 188.0,
            rating = 4.9,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 877,
            lat = -5.990,
            lng = 39.370,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_hotel_riu_jambo",
            title = "Hotel Riu Jambo",
            location = "Zanzibar",
            country = "Tanzania",
            description = "4-star hotel in Zanzibar.",
            pricePerNight = 250.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 2151,
            lat = -5.750,
            lng = 39.290,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_golden_tulip_zanzibar",
            title = "Golden Tulip Zanzibar Resort",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Casual resort with a rooftop lounge.",
            pricePerNight = 132.0,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Beach access"),
            reviewsCount = 1070,
            lat = -6.110,
            lng = 39.210,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_lux_marijani",
            title = "LUX Marijani, Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "5-star beachfront resort.",
            pricePerNight = 450.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 525,
            lat = -5.970,
            lng = 39.360,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_tembo_house_hotel",
            title = "Tembo House Hotel",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Casual beachfront hotel with a pool.",
            pricePerNight = 200.0,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free breakfast", "Free Wi-Fi", "Beach access"),
            reviewsCount = 2811,
            lat = -6.160,
            lng = 39.190,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_essque_zalu",
            title = "Essque Zalu Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Posh beach resort with bars & dining.",
            pricePerNight = 241.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 902,
            lat = -5.730,
            lng = 39.300,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_the_residence_zanzibar",
            title = "The Residence Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Posh beachfront resort with a spa.",
            pricePerNight = 687.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 854,
            lat = -6.280,
            lng = 39.460,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_zanzibar_view_hotel",
            title = "Zanzibar View Hotel",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Hotel in Zanzibar with a pool and free breakfast.",
            pricePerNight = 111.0,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 29,
            lat = -6.120,
            lng = 39.220,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_wellworth_zanzibar_beach_resort",
            title = "Wellworth Zanzibar Beach Resort",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Relaxed beachfront resort with a pool.",
            pricePerNight = 93.3,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 1318,
            lat = -6.100,
            lng = 39.230,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_zanzibella_hotel_spa",
            title = "Zanzibella Hotel & SPA",
            location = "Zanzibar",
            country = "Tanzania",
            description = "5-star hotel & SPA.",
            pricePerNight = 293.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 626,
            lat = -5.990,
            lng = 39.380,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_royal_zanzibar_beach_resort",
            title = "Royal Zanzibar Beach Resort",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Casual beach resort with dining & pools.",
            pricePerNight = 1034.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 1975,
            lat = -5.730,
            lng = 39.300,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_breezes_beach_club",
            title = "Breezes Beach Club & Spa Zanzibar",
            location = "Zanzibar",
            country = "Tanzania",
            description = "Genteel beach resort with a luxe spa.",
            pricePerNight = 711.8,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 431,
            lat = -6.130,
            lng = 39.520,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_turaco_nungwi_resort",
            title = "Turaco Nungwi Resort, a Tribute Portfolio Hotel",
            location = "Nungwi",
            country = "Tanzania",
            description = "4-star hotel in Nungwi.",
            pricePerNight = 527.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 391,
            lat = -5.730,
            lng = 39.300,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_lemaiyan_suites",
            title = "Lemaiyan Suites",
            location = "Naivasha",
            country = "Kenya",
            description = "Experience elegant rooms, premium amenities, and stunning views.",
            pricePerNight = 115.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 783,
            lat = -0.710,
            lng = 36.430,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_el_sueno_homestay",
            title = "El Sueno Homestay",
            location = "Naivasha",
            country = "Kenya",
            description = "Cool and serene environment.",
            pricePerNight = 49.75,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi"),
            reviewsCount = 126,
            lat = -0.720,
            lng = 36.420,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_lake_naivasha_resort",
            title = "Lake Naivasha Resort",
            location = "Naivasha",
            country = "Kenya",
            description = "3-star hotel in Naivasha.",
            pricePerNight = 298.0,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 5865,
            lat = -0.730,
            lng = 36.410,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_sweet_lake_resort",
            title = "Sweet Lake Resort",
            location = "Naivasha",
            country = "Kenya",
            description = "Unassuming hotel with dining & a bar.",
            pricePerNight = 47.68,
            rating = 3.9,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 1672,
            lat = -0.740,
            lng = 36.400,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_burchs_resort_naivasha",
            title = "Burch's Resort Naivasha",
            location = "Naivasha",
            country = "Kenya",
            description = "Woodsy resort with cottages & tents.",
            pricePerNight = 110.0,
            rating = 4.0,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 1342,
            lat = -0.750,
            lng = 36.390,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_lake_naivasha_lazarus_lodge",
            title = "Lake Naivasha Lazarus Lodge",
            location = "Naivasha",
            country = "Kenya",
            description = "Hotel in Naivasha.",
            pricePerNight = 68.66,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 152,
            lat = -0.730,
            lng = 36.420,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_triple_eden_naivasha",
            title = "Triple Eden Naivasha Hotel",
            location = "Naivasha",
            country = "Kenya",
            description = "Resort hotel in Naivasha.",
            pricePerNight = 21.90,
            rating = 3.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 264,
            lat = -0.740,
            lng = 36.430,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_fina_gardens_resort",
            title = "Fina Gardens Resort Naivasha",
            location = "Naivasha",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 42.66,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Wi-Fi"),
            reviewsCount = 319,
            lat = -0.720,
            lng = 36.410,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_eseriani_the_resort",
            title = "Eseriani The Resort",
            location = "Naivasha",
            country = "Kenya",
            description = "3-star hotel in Naivasha.",
            pricePerNight = 76.70,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 799,
            lat = -0.710,
            lng = 36.400,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_the_nest_boutique",
            title = "The Nest Boutique, Naivasha",
            location = "Naivasha",
            country = "Kenya",
            description = "Bed & breakfast in Naivasha.",
            pricePerNight = 169.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 468,
            lat = -0.730,
            lng = 36.390,
            availabilityStatus = "Available",
            hotelClass = "Bed & breakfast"
        ),
        StayItem(
            id = "stay_lake_naivasha_crescent_camp",
            title = "Lake Naivasha Crescent Camp",
            location = "Naivasha",
            country = "Kenya",
            description = "Hotel in Naivasha.",
            pricePerNight = 473.0,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 1573,
            lat = -0.750,
            lng = 36.380,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_chambai_safari_hotel",
            title = "Chambai Safari Hotel",
            location = "Naivasha",
            country = "Kenya",
            description = "3-star hotel in Naivasha.",
            pricePerNight = 127.0,
            rating = 3.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 420,
            lat = -0.710,
            lng = 36.390,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_panorama_park_hotel",
            title = "Panorama Park Hotel",
            location = "Naivasha",
            country = "Kenya",
            description = "Casual lakeside hotel with a pool & spa.",
            pricePerNight = 128.0,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 2105,
            lat = -0.700,
            lng = 36.400,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_elsamere_lodge",
            title = "Elsamere Lodge Naivasha",
            location = "Naivasha",
            country = "Kenya",
            description = "3-star hotel in Naivasha.",
            pricePerNight = 65.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 573,
            lat = -0.780,
            lng = 36.350,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_lake_naivasha_country_club",
            title = "Lake Naivasha Country Club",
            location = "Naivasha",
            country = "Kenya",
            description = "Hotel in Naivasha.",
            pricePerNight = 436.0,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1843,
            lat = -0.760,
            lng = 36.360,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_merica_hotel_nakuru",
            title = "Merica Hotel Nakuru",
            location = "Nakuru",
            country = "Kenya",
            description = "Relaxed hotel with dining & a pool.",
            pricePerNight = 91.58,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 3035,
            lat = -0.300,
            lng = 36.070,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_kivu_resort",
            title = "Kivu Resort",
            location = "Nakuru",
            country = "Kenya",
            description = "Relaxed resort with a pool & dining.",
            pricePerNight = 35.20,
            rating = 3.9,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Wi-Fi"),
            reviewsCount = 1468,
            lat = -0.290,
            lng = 36.080,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_sleepway_cottages",
            title = "Sleepway Cottages, Nakuru",
            location = "Nakuru",
            country = "Kenya",
            description = "Casual hotel with a low-key restaurant.",
            pricePerNight = 18.74,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 378,
            lat = -0.280,
            lng = 36.075,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_the_ole_ken_hotel",
            title = "The Ole-Ken Hotel, Nakuru",
            location = "Nakuru",
            country = "Kenya",
            description = "Casual hotel with dining & a bar.",
            pricePerNight = 120.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1923,
            lat = -0.310,
            lng = 36.060,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_lake_nakuru_sopa_lodge",
            title = "Lake Nakuru Sopa Lodge",
            location = "Nakuru",
            country = "Kenya",
            description = "Hotel in Nakuru.",
            pricePerNight = 529.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 1135,
            lat = -0.350,
            lng = 36.100,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_lake_nakuru_lodge",
            title = "Lake Nakuru Lodge",
            location = "Nakuru",
            country = "Kenya",
            description = "Relaxed lodging with a restaurant & pool.",
            pricePerNight = 483.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1131,
            lat = -0.360,
            lng = 36.090,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_eagle_palace_hotel",
            title = "Eagle Palace Hotel, Nakuru",
            location = "Nakuru",
            country = "Kenya",
            description = "Laid-back hotel with a restaurant.",
            pricePerNight = 77.36,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1558,
            lat = -0.270,
            lng = 36.085,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_eldorado_rustic_hotel",
            title = "Eldorado Rustic Hotel & Gardens",
            location = "Nakuru",
            country = "Kenya",
            description = "Resort hotel in Nakuru.",
            pricePerNight = 67.50,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 544,
            lat = -0.260,
            lng = 36.095,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_chester_hotel",
            title = "Chester Hotel",
            location = "Nakuru",
            country = "Kenya",
            description = "Relaxed hotel with dining & free Wi-Fi.",
            pricePerNight = 130.0,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 793,
            lat = -0.280,
            lng = 36.100,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_top_cliff_lodge",
            title = "Top Cliff Lodge",
            location = "Nakuru",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 116.0,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 701,
            lat = -0.270,
            lng = 36.110,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_masai_lodge",
            title = "Masai Lodge",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed lodge with dining & a pool.",
            pricePerNight = 140.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 3103,
            lat = -1.390,
            lng = 36.850,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_red_buffalo_house_hotel",
            title = "Red Buffalo House Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel offering 2 bars & dining.",
            pricePerNight = 17.55,
            rating = 4.0,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 478,
            lat = -1.380,
            lng = 36.840,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_ololo_safari_lodge",
            title = "Ololo Safari Lodge & Farm",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale lodge in Nairobi National Park.",
            pricePerNight = 982.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 416,
            lat = -1.410,
            lng = 36.860,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_esidai_spa_and_resort",
            title = "Esidai spa and resort",
            location = "Nairobi",
            country = "Kenya",
            description = "Resort hotel in Nairobi.",
            pricePerNight = 365.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 33,
            lat = -1.370,
            lng = 36.830,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_the_emakoko",
            title = "The Emakoko",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel in Nairobi National Park.",
            pricePerNight = 2240.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 207,
            lat = -1.400,
            lng = 36.870,
            availabilityStatus = "Available",
            hotelClass = "Lodge"
        ),
        StayItem(
            id = "stay_olsupat_lodge_kenya",
            title = "Olsupat Lodge Kenya",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 96.79,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 404,
            lat = -1.390,
            lng = 36.840,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_nyati_hill_cottages",
            title = "Nyati Hill Cottages",
            location = "Nairobi",
            country = "Kenya",
            description = "Resort hotel in Nairobi.",
            pricePerNight = 144.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Wi-Fi", "Kitchen"),
            reviewsCount = 66,
            lat = -1.380,
            lng = 36.850,
            availabilityStatus = "Available",
            hotelClass = "Resort hotel"
        ),
        StayItem(
            id = "stay_the_panari_hotel",
            title = "The Panari Hotel, Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Chic lodging with a bar & an ice rink.",
            pricePerNight = 106.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 7820,
            lat = -1.320,
            lng = 36.860,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_the_boma_nairobi",
            title = "The Boma Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale hotel with a spa & rooftop pool.",
            pricePerNight = 69.08,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 5544,
            lat = -1.310,
            lng = 36.820,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_hilton_garden_inn_nairobi",
            title = "Hilton Garden Inn Nairobi Airport",
            location = "Nairobi",
            country = "Kenya",
            description = "Sleek hotel with an infinity pool.",
            pricePerNight = 238.42,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 4672,
            lat = -1.330,
            lng = 36.900,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_argyle_grand_hotel",
            title = "Argyle Grand Hotel Nairobi Airport",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel near the airport.",
            pricePerNight = 247.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 1527,
            lat = -1.340,
            lng = 36.910,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_kozi_nairobi_airport",
            title = "Kozi Nairobi Airport",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel with a pool & a gym.",
            pricePerNight = 53.23,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 1031,
            lat = -1.350,
            lng = 36.920,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_nairobi_transit_lounge",
            title = "Nairobi Transit Lounge",
            location = "Nairobi",
            country = "Kenya",
            description = "Modest hotel with a terrace & free Wi-Fi.",
            pricePerNight = 28.42,
            rating = 4.0,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 148,
            lat = -1.320,
            lng = 36.880,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_prideinn_plaza_nairobi",
            title = "PrideInn Plaza Nairobi Airport",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel with a bar & a restaurant.",
            pricePerNight = 122.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1470,
            lat = -1.360,
            lng = 36.930,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_airport_landing_hotel",
            title = "AIRPORT LANDING HOTEL",
            location = "Nairobi",
            country = "Kenya",
            description = "Informal hotel offering dining & a gym.",
            pricePerNight = 81.89,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 1266,
            lat = -1.330,
            lng = 36.890,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_kings_premier_inn",
            title = "Kings Premier Inn",
            location = "Nairobi",
            country = "Kenya",
            description = "Hotel in Nairobi.",
            pricePerNight = 29.92,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Airport shuttle"),
            reviewsCount = 169,
            lat = -1.310,
            lng = 36.870,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_crowne_plaza_nairobi_airport",
            title = "Crowne Plaza Nairobi Airport by IHG",
            location = "Nairobi",
            country = "Kenya",
            description = "Sleek airport hotel with dining & a pool.",
            pricePerNight = 282.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 2430,
            lat = -1.340,
            lng = 36.925,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_zarita_boutique_airport",
            title = "Zarita Boutique Airport Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Boutique airport hotel.",
            pricePerNight = 29.04,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Wi-Fi"),
            reviewsCount = 208,
            lat = -1.330,
            lng = 36.930,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_67_airport_hotel",
            title = "67 Airport Hotel Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel with a pool & views.",
            pricePerNight = 134.0,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 3444,
            lat = -1.360,
            lng = 36.940,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_emara_ole_sereni",
            title = "Emara Ole-Sereni",
            location = "Nairobi",
            country = "Kenya",
            description = "Posh hotel next to Nairobi National Park.",
            pricePerNight = 205.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 8670,
            lat = -1.330,
            lng = 36.850,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_balis_best_resort",
            title = "Bali's Best Resort, Nairobi Kenya",
            location = "Nairobi",
            country = "Kenya",
            description = "2-star hotel in Nairobi.",
            pricePerNight = 59.46,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Airport shuttle"),
            reviewsCount = 1847,
            lat = -1.340,
            lng = 36.860,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_the_curve_by_the_park",
            title = "The Curve By The Park",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel in Nairobi.",
            pricePerNight = 124.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 169,
            lat = -1.350,
            lng = 36.870,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_nairobi_airport_resthouse",
            title = "Nairobi Airport Resthouse",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 47.62,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 121,
            lat = -1.360,
            lng = 36.880,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_ole_sereni",
            title = "Ole-Sereni",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale hotel with restaurants & a pool.",
            pricePerNight = 168.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 1570,
            lat = -1.320,
            lng = 36.840,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_park_place_hotel",
            title = "Park Place Hotel Karen Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Tranquil lodging with a pool & gardens.",
            pricePerNight = 84.02,
            rating = 3.9,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 853,
            lat = -1.370,
            lng = 36.750,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_eka_hotel_nairobi",
            title = "Eka Hotel Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Contemporary hotel with 2 restaurants.",
            pricePerNight = 156.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 6699,
            lat = -1.320,
            lng = 36.870,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_pori_city_hotel",
            title = "Pori City Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel in Nairobi.",
            pricePerNight = 69.80,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 432,
            lat = -1.310,
            lng = 36.880,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_nairobi_airport_apartments",
            title = "Nairobi Airport Apartments",
            location = "Nairobi",
            country = "Kenya",
            description = "Basic guesthouse offering free breakfast.",
            pricePerNight = 105.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Air-conditioned"),
            reviewsCount = 78,
            lat = -1.300,
            lng = 36.890,
            availabilityStatus = "Available",
            hotelClass = "Serviced accommodation"
        ),
        StayItem(
            id = "stay_airport_vista_nairobi",
            title = "Airport Vista Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Bed & breakfast in Nairobi.",
            pricePerNight = 77.56,
            rating = 0.0,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 0,
            lat = -1.320,
            lng = 36.900,
            availabilityStatus = "Available",
            hotelClass = "Bed & breakfast"
        ),
        StayItem(
            id = "stay_radisson_blu_upper_hill",
            title = "Radisson Blu Hotel, Nairobi Upper Hill",
            location = "Nairobi",
            country = "Kenya",
            description = "Polished lodging with African dining.",
            pricePerNight = 258.52,
            rating = 4.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 12597,
            lat = -1.300,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_la_maison_royale_south_c",
            title = "La Maison Royale- South C",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a rooftop pool.",
            pricePerNight = 174.50,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 360,
            lat = -1.320,
            lng = 36.830,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_east_airport_accommodations",
            title = "East Airport Accomodations",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a pool & a gym.",
            pricePerNight = 32.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Wi-Fi"),
            reviewsCount = 66,
            lat = -1.325,
            lng = 36.890,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_airport_view_plaza_hotel",
            title = "Airport View Plaza Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual budget hotel with a restaurant.",
            pricePerNight = 25.0,
            rating = 3.8,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 457,
            lat = -1.330,
            lng = 36.885,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_tamarind_tree_hotel",
            title = "Tamarind Tree Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Refined hotel with a pool & dining.",
            pricePerNight = 240.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 5124,
            lat = -1.335,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_nairobi_inn",
            title = "Nairobi Inn",
            location = "Nairobi",
            country = "Kenya",
            description = "Informal hotel with a restaurant.",
            pricePerNight = 35.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Airport shuttle"),
            reviewsCount = 97,
            lat = -1.300,
            lng = 36.850,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_aero_club_of_east_africa",
            title = "Aero Club Of East Africa",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel offering a pool & dining.",
            pricePerNight = 192.0,
            rating = 4.5,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 762,
            lat = -1.315,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_jkia_airport_hotel",
            title = "JKIA AIRPORT HOTEL",
            location = "Nairobi",
            country = "Kenya",
            description = "2-star hotel.",
            pricePerNight = 30.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Airport shuttle"),
            reviewsCount = 469,
            lat = -1.350,
            lng = 36.930,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_dafam_hotel_kenya",
            title = "Dafam Hotel Kenya",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a restaurant.",
            pricePerNight = 25.0,
            rating = 3.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Airport shuttle"),
            reviewsCount = 748,
            lat = -1.340,
            lng = 36.900,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_weston_hotel_nairobi",
            title = "Weston Hotel Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Upmarket lodging with dining & a spa.",
            pricePerNight = 165.0,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 3602,
            lat = -1.315,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_four_points_by_sheraton_airport",
            title = "Four Points by Sheraton Nairobi Airport",
            location = "Nairobi",
            country = "Kenya",
            description = "Refined airport hotel with dining.",
            pricePerNight = 180.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 5053,
            lat = -1.330,
            lng = 36.920,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_hotel_kepler",
            title = "HOTEL KEPLER ( Airport north road) Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 45.0,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 400,
            lat = -1.320,
            lng = 36.890,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_summerdale_inn",
            title = "Summerdale Inn",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel offering free breakfast.",
            pricePerNight = 121.0,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1081,
            lat = -1.310,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_razana_hotel",
            title = "Razana Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with city views & dining.",
            pricePerNight = 95.36,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Wi-Fi"),
            reviewsCount = 755,
            lat = -1.310,
            lng = 36.820,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_hotel_rio",
            title = "Hotel Rio",
            location = "Nairobi",
            country = "Kenya",
            description = "Informal hotel with a restaurant.",
            pricePerNight = 72.55,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 1510,
            lat = -1.300,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_saab_royale_hotel",
            title = "Saab Royale Hotel - South C",
            location = "Nairobi",
            country = "Kenya",
            description = "Genteel hotel offering dining & parking.",
            pricePerNight = 90.48,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 421,
            lat = -1.320,
            lng = 36.830,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_nairobi_upper_hill_hotel",
            title = "Nairobi Upper Hill Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Business hotel with a French restaurant.",
            pricePerNight = 45.0,
            rating = 3.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Airport shuttle"),
            reviewsCount = 345,
            lat = -1.295,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_mercure_nairobi_upper_hill",
            title = "Mercure Nairobi Upper Hill",
            location = "Nairobi",
            country = "Kenya",
            description = "Refined hotel with dining & a pool.",
            pricePerNight = 186.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 529,
            lat = -1.290,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_best_western_nairobi_upper_hill",
            title = "Best Western Nairobi, Upper Hill",
            location = "Nairobi",
            country = "Kenya",
            description = "Informal hotel with dining & a gym.",
            pricePerNight = 80.72,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 286,
            lat = -1.285,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_upperhill_blueberry_hotel",
            title = "Upperhill Blueberry Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 43.0,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Wi-Fi"),
            reviewsCount = 186,
            lat = -1.295,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_holiday_inn_two_rivers",
            title = "Holiday Inn Nairobi Two Rivers Mall by IHG",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a pool & 24/7 dining.",
            pricePerNight = 107.29,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 982,
            lat = -1.210,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_hemak_suites_hotel",
            title = "Hemak Suites Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel with a steakhouse.",
            pricePerNight = 31.11,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 405,
            lat = -1.280,
            lng = 36.820,
            availabilityStatus = "Available",
            hotelClass = "Hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_chester_hotel_and_suites",
            title = "Chester Hotel & Suites Nairobi, City Centre CBD",
            location = "Nairobi",
            country = "Kenya",
            description = "Informal apartment hotel with a pool.",
            pricePerNight = 76.37,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 672,
            lat = -1.285,
            lng = 36.820,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_jamia_central_hotel",
            title = "Jamia Central Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 53.93,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Restaurant"),
            reviewsCount = 391,
            lat = -1.280,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_kenya_comfort_hotel",
            title = "Kenya Comfort Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a terrace & city views.",
            pricePerNight = 46.60,
            rating = 3.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Breakfast", "Wi-Fi", "Parking"),
            reviewsCount = 565,
            lat = -1.285,
            lng = 36.825,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_after_40_hotel",
            title = "After 40 Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with informal dining.",
            pricePerNight = 82.73,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 1369,
            lat = -1.280,
            lng = 36.822,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_inks_hotel",
            title = "INKS HOTEL",
            location = "Nairobi",
            country = "Kenya",
            description = "Low-key budget hotel with a restaurant.",
            pricePerNight = 13.96,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Breakfast", "Wi-Fi", "Parking"),
            reviewsCount = 822,
            lat = -1.282,
            lng = 36.828,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_ambassadeur_hotel_nairobi",
            title = "The Ambassadeur Hotel Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Laid-back lodging with dining & a bar.",
            pricePerNight = 165.0,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 4073,
            lat = -1.284,
            lng = 36.824,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_best_western_plus_meridian",
            title = "Best Western Plus Meridian Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a rooftop pool.",
            pricePerNight = 82.56,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 4306,
            lat = -1.280,
            lng = 36.820,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_marble_arch_hotel",
            title = "Marble Arch Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel with a restaurant & a bar.",
            pricePerNight = 62.91,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 1153,
            lat = -1.283,
            lng = 36.826,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_clarion_hotel_nairobi",
            title = "Clarion Hotel Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed lodging with dining & a gym.",
            pricePerNight = 75.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 2204,
            lat = -1.280,
            lng = 36.818,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_hotel_mokka_city",
            title = "Hotel Mokka City",
            location = "Nairobi",
            country = "Kenya",
            description = "Hotel in Nairobi.",
            pricePerNight = 76.78,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free Wi-Fi", "Air-conditioned", "Breakfast", "Wi-Fi"),
            reviewsCount = 145,
            lat = -1.282,
            lng = 36.822,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_hotel_boulevard_nairobi",
            title = "Hotel Boulevard Nairobi, City Centre CBD",
            location = "Nairobi",
            country = "Kenya",
            description = "Understated lodging with an outdoor pool.",
            pricePerNight = 127.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 2438,
            lat = -1.275,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_space_international_hotel",
            title = "Space International Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Unassuming hotel with a restaurant.",
            pricePerNight = 61.41,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Wi-Fi"),
            reviewsCount = 67,
            lat = -1.286,
            lng = 36.828,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_hotel_embassy",
            title = "HOTEL EMBASSY - Nairobi, Kenya",
            location = "Nairobi",
            country = "Kenya",
            description = "Simple lodging offering a restaurant.",
            pricePerNight = 40.89,
            rating = 4.0,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 552,
            lat = -1.285,
            lng = 36.825,
            availabilityStatus = "Available",
            hotelClass = "2-star hotel"
        ),
        StayItem(
            id = "stay_lagos_hotel",
            title = "Lagos Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Low-key hotel with a restaurant/bar.",
            pricePerNight = 69.81,
            rating = 4.1,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 667,
            lat = -1.284,
            lng = 36.824,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_sarova_stanley_hotel",
            title = "Sarova Stanley Hotel, Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale lodging with dining & a spa.",
            pricePerNight = 143.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 9428,
            lat = -1.283,
            lng = 36.823,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_nairobi_safari_club",
            title = "Nairobi Safari Club by Swiss-Belhotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Upmarket all-suite hotel with dining.",
            pricePerNight = 103.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 2356,
            lat = -1.280,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_delfirm_hotel",
            title = "DELFIRM HOTEL",
            location = "Nairobi",
            country = "Kenya",
            description = "Open 24 hours.",
            pricePerNight = 36.59,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 382,
            lat = -1.285,
            lng = 36.830,
            availabilityStatus = "Available",
            hotelClass = "Restaurant"
        ),
        StayItem(
            id = "stay_park_inn_by_radisson_westlands",
            title = "Park Inn By Radisson Nairobi Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "Modern hotel with a rooftop pool & bar.",
            pricePerNight = 154.12,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 5156,
            lat = -1.265,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_ibis_styles_nairobi_westlands",
            title = "ibis Styles Nairobi Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual lodging with a rooftop bar.",
            pricePerNight = 50.17,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 6667,
            lat = -1.260,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_golden_tulip_westlands_nairobi",
            title = "Golden Tulip Westlands Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Simple hotel with a pool & dining.",
            pricePerNight = 120.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 4094,
            lat = -1.265,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_la_maison_royale_hotel",
            title = "La Maison Royale Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Polished hotel with dining & a spa.",
            pricePerNight = 66.65,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 1632,
            lat = -1.260,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_hyatt_regency_nairobi_westlands",
            title = "Hyatt Regency Nairobi Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel in Westlands.",
            pricePerNight = 269.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 544,
            lat = -1.265,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_prideinn_azure_hotel",
            title = "PrideInn Azure Hotel Nairobi, Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale hotel with a casino & restaurant.",
            pricePerNight = 148.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 5802,
            lat = -1.260,
            lng = 36.790,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_prideinn_westlands_luxury",
            title = "PrideInn Westlands Luxury Boutique Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Relaxed hotel with free breakfast.",
            pricePerNight = 128.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1873,
            lat = -1.265,
            lng = 36.795,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_jacaranda_hotel_nairobi",
            title = "Jacaranda Hotel - Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Elegant lodging with a spa & dining.",
            pricePerNight = 155.0,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 2434,
            lat = -1.260,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_skynest_residences_by_cityblue",
            title = "Skynest Residences by CityBlue, Nairobi Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "Modern apartment hotel with city views.",
            pricePerNight = 168.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 702,
            lat = -1.260,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_the_crossroads_hotel",
            title = "The Crossroads Hotel",
            location = "Nairobi",
            country = "Kenya",
            description = "Informal hotel offering dining & a bar.",
            pricePerNight = 106.0,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 447,
            lat = -1.265,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_joy_palace_hotel",
            title = "Joy Palace Hotel & accommodation",
            location = "Nairobi",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 50.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 306,
            lat = -1.270,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_movenpick_nairobi_hotel",
            title = "Mövenpick Nairobi Hotel & Residences",
            location = "Nairobi",
            country = "Kenya",
            description = "Luxury lodging with revolving restaurant.",
            pricePerNight = 236.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 8472,
            lat = -1.265,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_jw_marriott_hotel_nairobi",
            title = "JW Marriott Hotel Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel in Nairobi.",
            pricePerNight = 388.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 1301,
            lat = -1.270,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_red_ruby_hotel",
            title = "Red Ruby Hotel, Parklands",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with dining & a bar.",
            pricePerNight = 48.87,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 457,
            lat = -1.265,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_sankara_nairobi",
            title = "Sankara Nairobi, Autograph Collection",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale hotel with a rooftop pool.",
            pricePerNight = 333.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 4750,
            lat = -1.265,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_pan_pacific_serviced_suites",
            title = "Pan Pacific Serviced Suites Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel.",
            pricePerNight = 227.0,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 309,
            lat = -1.270,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_villa_rosa_kempinski",
            title = "Villa Rosa Kempinski",
            location = "Nairobi",
            country = "Kenya",
            description = "High-end hotel with a spa & cigar lounge.",
            pricePerNight = 344.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free Wi-Fi"),
            reviewsCount = 10807,
            lat = -1.270,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_parklands_shade_hotel",
            title = "Parklands Shade Hotel K1 Clubhouse",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with a restaurant & a bar.",
            pricePerNight = 60.0,
            rating = 4.3,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Wi-Fi", "Restaurant"),
            reviewsCount = 364,
            lat = -1.265,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_lotos_suites_nairobi",
            title = "Lotos Suites Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Unfussy hotel with a rooftop restaurant.",
            pricePerNight = 99.43,
            rating = 4.3,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 1290,
            lat = -1.260,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_best_western_premier_westlands",
            title = "Best Western Premier Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "Upscale hotel with a rooftop bar.",
            pricePerNight = 167.0,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 616,
            lat = -1.265,
            lng = 36.800,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_hyatt_place_nairobi_westlands",
            title = "Hyatt Place Nairobi Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel in Westlands.",
            pricePerNight = 258.0,
            rating = 4.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 264,
            lat = -1.260,
            lng = 36.805,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_raha_suites_westlands",
            title = "Raha Suites Westlands Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Casual hotel with dining & city views.",
            pricePerNight = 56.87,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 151,
            lat = -1.270,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_hyatt_house_nairobi_westlands",
            title = "Hyatt House Nairobi Westlands",
            location = "Nairobi",
            country = "Kenya",
            description = "5-star hotel.",
            pricePerNight = 285.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 69,
            lat = -1.265,
            lng = 36.815,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_dusit_princess_hotel_residences",
            title = "Dusit Princess Hotel Residences Nairobi",
            location = "Nairobi",
            country = "Kenya",
            description = "Premium hotel with a rooftop pool.",
            pricePerNight = 171.0,
            rating = 4.7,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 791,
            lat = -1.260,
            lng = 36.810,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_kenya_safari_lodge_nakuru",
            title = "Kenya Safari Lodge",
            location = "Lake Nakuru National Park",
            country = "Kenya",
            description = "Lodge in Lake Nakuru National Park.",
            pricePerNight = 125.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 2174,
            lat = -0.366,
            lng = 36.050,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_comfy_inn_hotel_eldoret",
            title = "COMFY INN HOTEL ELDORET",
            location = "Eldoret",
            country = "Kenya",
            description = "Informal hotel with free breakfast.",
            pricePerNight = 28.43,
            rating = 4.1,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 1150,
            lat = 0.514,
            lng = 35.269,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_noble_hotel_and_conference_centre",
            title = "Noble Hotel And Conference Centre Eldoret",
            location = "Eldoret",
            country = "Kenya",
            description = "Laid-back hotel with dining & a pool.",
            pricePerNight = 63.27,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 2104,
            lat = 0.520,
            lng = 35.280,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_boma_inn_eldoret",
            title = "Boma Inn Eldoret",
            location = "Eldoret",
            country = "Kenya",
            description = "Laid-back hotel with dining & a pool.",
            pricePerNight = 127.0,
            rating = 4.4,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 2822,
            lat = 0.510,
            lng = 35.275,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_eka_hotel_eldoret",
            title = "Eka Hotel",
            location = "Eldoret",
            country = "Kenya",
            description = "Understated hotel with a rooftop pool.",
            pricePerNight = 142.0,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1768,
            lat = 0.515,
            lng = 35.265,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_sirikwa_hotel",
            title = "Sirikwa Hotel",
            location = "Eldoret",
            country = "Kenya",
            description = "Informal hotel with dining & a pool.",
            pricePerNight = 107.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 2782,
            lat = 0.518,
            lng = 35.270,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_poa_place_resort",
            title = "Poa Place Resort",
            location = "Eldoret",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 73.04,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Breakfast"),
            reviewsCount = 1254,
            lat = 0.525,
            lng = 35.275,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_winstar_hotel",
            title = "Winstar Hotel",
            location = "Eldoret",
            country = "Kenya",
            description = "Casual hotel with a restaurant & a bar.",
            pricePerNight = 64.0,
            rating = 3.9,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free breakfast", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 974,
            lat = 0.516,
            lng = 35.268,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_fairways_hotel_kisumu",
            title = "Fairways Hotel Kisumu",
            location = "Kisumu",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 122.37,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 610,
            lat = -0.100,
            lng = 34.750,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_hotel_riversand",
            title = "Hotel Riversand",
            location = "Kisumu",
            country = "Kenya",
            description = "3-star hotel.",
            pricePerNight = 16.28,
            rating = 3.8,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free breakfast", "Free Wi-Fi", "Breakfast", "Wi-Fi"),
            reviewsCount = 535,
            lat = -0.105,
            lng = 34.755,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_wigot_gardens_hotel",
            title = "Wigot Gardens Hotel Kisumu",
            location = "Kisumu",
            country = "Kenya",
            description = "Hotel in Kisumu.",
            pricePerNight = 65.0,
            rating = 4.2,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free Wi-Fi", "Air-conditioned"),
            reviewsCount = 1059,
            lat = -0.095,
            lng = 34.760,
            availabilityStatus = "Available",
            hotelClass = "Hotel"
        ),
        StayItem(
            id = "stay_ciala_resort_kisumu",
            title = "Ciala Resort Kisumu",
            location = "Kisumu",
            country = "Kenya",
            description = "4-star hotel in Kisumu.",
            pricePerNight = 175.0,
            rating = 4.6,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 2813,
            lat = -0.090,
            lng = 34.765,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_acacia_premier_hotel",
            title = "Acacia Premier Hotel, Kisumu",
            location = "Kisumu",
            country = "Kenya",
            description = "Refined lakeside hotel with dining.",
            pricePerNight = 205.0,
            rating = 4.5,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 3960,
            lat = -0.100,
            lng = 34.750,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_sarova_imperial_hotel_kisumu",
            title = "Sarova Imperial Hotel, Kisumu",
            location = "Kisumu",
            country = "Kenya",
            description = "Modern hotel with 2 restaurants & a pool.",
            pricePerNight = 165.0,
            rating = 4.2,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Free parking", "Free Wi-Fi", "Air-conditioned", "Breakfast"),
            reviewsCount = 1202,
            lat = -0.102,
            lng = 34.752,
            availabilityStatus = "Available",
            hotelClass = "3-star hotel"
        ),
        StayItem(
            id = "stay_mahali_mzuri",
            title = "Mahali Mzuri",
            location = "Maasai Mara",
            country = "Kenya",
            description = "Posh all-inclusive lodge in a reserve. Your front row seat to Africa's thrilling wildlife with 5-star service & incredible dining.",
            pricePerNight = 6210.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1549144511-f099e773c147?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Spa", "Pool", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 161,
            lat = -1.332,
            lng = 35.150,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel",
            isSponsored = true
        ),
        StayItem(
            id = "stay_mara_serena",
            title = "Mara Serena Safari Lodge",
            location = "Maasai Mara National Reserve",
            country = "Kenya",
            description = "5-star hotel located in Maasai Mara National Reserve.",
            pricePerNight = 1200.0,
            rating = 4.6,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 1526,
            lat = -1.411,
            lng = 35.013,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
        ),
        StayItem(
            id = "stay_sarova_mara",
            title = "Sarova Mara Game Camp",
            location = "Maasai Mara",
            country = "Kenya",
            description = "Posh all-inclusive lodge with a pool.",
            pricePerNight = 1120.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 2048,
            lat = -1.455,
            lng = 35.154,
            availabilityStatus = "Available",
            hotelClass = "4-star hotel"
        ),
        StayItem(
            id = "stay_seronera_wildlife_lodge",
            title = "Seronera Wildlife Lodge",
            location = "Serengeti National Park",
            country = "Tanzania",
            description = "Indoor lodging with a pool in Serengeti.",
            pricePerNight = 1386.74,
            rating = 4.4,
            imageResName = "img_safari_hero",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1590523741831-ab7e8caa41d9?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Free parking", "Free breakfast", "Free Wi-Fi"),
            reviewsCount = 189,
            lat = -2.433,
            lng = 34.821,
            availabilityStatus = "Available",
            hotelClass = "Hotel",
            isSponsored = true,
            bookingSource = "Booking.com"
        ),
        StayItem(
            id = "stay_four_seasons_serengeti",
            title = "Four Seasons Safari Lodge Serengeti",
            location = "Serengeti National Park",
            country = "Tanzania",
            description = "Luxury hotel with a spa & a wine cellar.",
            pricePerNight = 2500.0,
            rating = 4.7,
            imageResName = "img_luxury_lodge",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80"
            ),
            amenities = listOf("Pool", "Spa", "Free parking", "Free breakfast"),
            reviewsCount = 728,
            lat = -2.251,
            lng = 34.852,
            availabilityStatus = "Available",
            hotelClass = "5-star hotel"
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
            description = "The ultimate African savanna odyssey. Follow the millions of wildebeest, zebras, and gazelles as they brave river crossings and predators across the transboundary plains of East Africa.",
            lat = -2.333,
            lng = 34.833
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
            description = "Explore the largest unbroken caldera in the world, home to over 30,000 animals including Africa's densest concentration of lions, elephants, and rare black rhinos.",
            lat = -3.200,
            lng = 35.500
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
            description = "Immerse yourself in history, aroma, and ocean waves. Tour historic Stone Town, sail to pristine sandbanks on a traditional wooden boat, and swim with sea turtles.",
            lat = -6.100,
            lng = 39.300
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
            description = "Ascend silently into the cold savanna morning. Watch the golden sunrise ignite the acacia tops and view massive herds of elephants and wildebeest moving below. Concludes with a grand champagne bush breakfast.",
            lat = -2.153,
            lng = 34.685
        ),
        ExperienceItem(
            id = "exp_maasai_culture",
            title = "Authentic Maasai Cultural Journey",
            location = "Mara Manyatta, Kenya",
            durationHours = 2.0,
            price = 60.0,
            rating = 4.88,
            imageResName = "img_luxury_lodge",
            description = "Spend a half-day welcomed into a traditional Manyatta village. Learn ancient fire-starting techniques, participate in the Adumu jumping dance, see beautiful beaded beadwork, and support local community schools.",
            lat = -1.488,
            lng = 35.155
        ),
        ExperienceItem(
            id = "exp_zanzibar_dhow",
            title = "Sunset Swahili Dhow Cruise & Feast",
            location = "Nungwi Swahili Coast, Zanzibar",
            durationHours = 4.0,
            price = 95.0,
            rating = 4.91,
            imageResName = "img_safari_hero",
            description = "Set sail on a handcrafted mahogany dhow as the equatorial sun dips below the turquoise horizon. Enjoy traditional Taarab live music on board and an organic wood-grilled rock lobster beach dinner.",
            lat = -5.733,
            lng = 39.294
        )
    )

    val swahiliPhrases = listOf(
        SwahiliPhrase("Hello / How are you?", "Habari yako?", "Ha-bar-ee ya-ko", "Greetings"),
        SwahiliPhrase("Welcome", "Karibu", "Ka-ree-boo", "Greetings"),
        SwahiliPhrase("Thank you", "Asante", "A-san-te", "Greetings"),
        SwahiliPhrase("Yes", "Ndiyo", "N-dee-yo", "Basics"),
        SwahiliPhrase("No", "Hapana", "Ha-pa-na", "Basics"),
        SwahiliPhrase("Please", "Tafadhali", "Ta-fad-ha-lee", "Basics"),
        SwahiliPhrase("Sorry / Excuse me", "Samahani", "Sa-ma-ha-nee", "Basics"),
        SwahiliPhrase("I am happy", "Nimefurahi", "Nee-me-foo-ra-hee", "Feelings"),
        SwahiliPhrase("Where is...?", "Iko wapi...?", "Ee-ko wa-pee", "Questions"),
        SwahiliPhrase("How much?", "Ni bei gani?", "Nee bay ga-nee", "Questions"),
        SwahiliPhrase("Water", "Maji", "Ma-jee", "Food & Drink"),
        SwahiliPhrase("Food", "Chakula", "Cha-koo-la", "Food & Drink"),
        SwahiliPhrase("Delicious!", "Tamu sana!", "Ta-moo sa-na", "Food & Drink"),
        SwahiliPhrase("Let's go!", "Twende!", "Twen-de", "Safari"),
        SwahiliPhrase("Lion", "Simba", "Sim-ba", "Safari"),
        SwahiliPhrase("Elephant", "Tembo", "Tem-bo", "Safari"),
        SwahiliPhrase("Cheetah", "Duma", "Doo-ma", "Safari"),
        SwahiliPhrase("Leopard", "Chui", "Choo-ee", "Safari"),
        SwahiliPhrase("Rhino", "Kifaru", "Kee-fa-roo", "Safari"),
        SwahiliPhrase("Hippo", "Kiboko", "Kee-bo-ko", "Safari"),
        SwahiliPhrase("Slowly", "Pole pole", "Po-le po-le", "Safari"),
        SwahiliPhrase("No problem", "Hakuna matata", "Ha-koo-na ma-ta-ta", "Safari"),
        SwahiliPhrase("Goodbye", "Kwaheri", "Kwa-hay-ree", "Greetings")
    )

    val packingList = listOf(
        // ESSENTIALS (Universal)
        PackingItem("Passport & Visa", "Travel documents are priority #1.", "Essentials", "description"),
        PackingItem("Yellow Fever Certificate", "Often required for entry in East Africa.", "Essentials", "verified_user"),
        PackingItem("Sunscreen (SPF 50+)", "The equatorial sun is intense.", "Health", "wb_sunny"),
        PackingItem("Insect Repellent", "With DEET for maximum protection.", "Health", "bug_report"),
        
        // ECOSYSTEM SPECIFIC
        PackingItem("Neutral Toned Clothing", "Khaki, green, or beige to blend in.", "Clothing", "checkroom", requiredForEcosystems = listOf("Savanna")),
        PackingItem("Light Rain Jacket", "Sudden tropical showers are common.", "Clothing", "umbrella", requiredForEcosystems = listOf("Rainforest", "Coastal")),
        PackingItem("Warm Fleece", "Early morning game drives are surprisingly cold.", "Clothing", "ac_unit", requiredForEcosystems = listOf("Savanna", "Highlands")),
        PackingItem("Sturdy Hiking Boots", "Essential for uneven terrain.", "Footwear", "terrain", requiredForEcosystems = listOf("Rainforest", "Highlands"), requiredForActivities = listOf("Walking Safari")),
        PackingItem("Sandals / Flip-flops", "For relaxing at the lodge or beach.", "Footwear", "wb_sunny", requiredForEcosystems = listOf("Coastal")),

        // ACTIVITY SPECIFIC
        PackingItem("Binoculars", "Critical for spotting distant wildlife.", "Gear", "visibility", requiredForActivities = listOf("Game Drive", "Walking Safari")),
        PackingItem("High-Zoom Camera", "Capture the big five in detail.", "Gear", "camera_alt", requiredForActivities = listOf("Game Drive")),
        PackingItem("Wide-Brimmed Hat", "Protects face and neck from sunburn.", "Clothing", "face", requiredForActivities = listOf("Game Drive", "Walking Safari")),
        PackingItem("Dry Bag", "Keeps electronics safe from splashes.", "Gear", "folder_zip", requiredForActivities = listOf("Dhow Cruise", "Boat Safari")),
        PackingItem("Swimwear", "For lodge pools or Indian Ocean dips.", "Clothing", "pool", requiredForActivities = listOf("Dhow Cruise", "Beach Relaxing")),
        
        // GENERAL SAFARI
        PackingItem("Headlamp / Flashlight", " lodges can be dark at night.", "Gear", "highlight"),
        PackingItem("Power Bank", "Keep your devices charged in the bush.", "Gear", "battery_charging_full"),
        PackingItem("Personal First Aid Kit", "Band-aids, antiseptic, and rehydration salts.", "Health", "medical_services")
    )
}
