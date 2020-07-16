package com.legacy.customer;

import com.legacy.customer.config.AddressTypes;
import com.legacy.customer.dto.AddressDto;
import com.legacy.customer.dto.CustomerDto;
import com.legacy.customer.dto.EnrichedCustomer;
import com.legacy.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableScheduling
public class CustomerApplication {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

	private final String createdBy = "mod-random-generator";

	@Autowired
	private CustomerService customerService;
	@Value("#{new Boolean('${simulator-enabled}')}")
	private Boolean simulatorEnabled;

	private final List<String> firstNames = Arrays.asList("Lilly", "Brittany", "Tayyibah", "Alisha", "Ava",
			"Tanya", "Keri", "Essa", "Diana", "Henri", "Ocean", "Katlyn", "Ariel", "Brodie", "Aniya",
			"Franklin", "Dougie", "Ellis", "Francesca", "Celine", "Joanne", "Arwen", "Jayce", "Usama",
			"Geraint", "Lana", "Zakaria", "Belle", "Monique", "Melanie", "Hattie", "Shyla", "Kristie",
			"Maggie", "Ayse", "Ella", "Lisa", "Kylo", "Maariya", "Caitlyn", "Jacqueline", "Caitlan",
			"Katrina", "Haley", "Addie", "Domas", "Charleigh", "Kenya", "Tom", "Geraldine", "Cian",
			"Travis", "Lillia", "Elli", "Adele", "Demi", "India", "Darius", "Aeryn", "Marianna", "Grant",
			"Tomasz", "Hywel", "Christie", "Xander", "Bo", "Liyana", "Jimi", "Hebe", "Montgomery", "Ffion",
			"Jaya", "Areebah", "Deacon", "Kieran", "Stewart", "Johnnie", "Lacie", "Iolo", "Hallie", "David",
			"Michelle", "Rayyan", "Camille", "Quentin", "Rachel", "Ethel", "Marshall", "Maiya", "Kirby",
			"Duncan", "Finnley", "Ismael", "Raees", "Sabina", "Cain", "Rumaysa",
			"Luella", "Jaden", "Paloma");
	private final List<String> lastNames = Arrays.asList( "Mellor", "Burch", "O'Sullivan", "Mcmahon", "Roberson",
			"Alfaro", "Calvert", "Austin", "Oconnor", "King", "Keith", "Acosta", "Bowers", "Ventura",
			"Ferry", "Cairns", "Dunn", "Pemberton", "Benson", "Handley", "Burrows", "Franco", "Kavanagh",
			"Harris", "Griffin", "Lewis", "Lugo", "Buxton", "Paine", "Boyer", "Rasmussen", "Perez", "Lennon",
			"Kearns", "Sweeney", "Callaghan", "Lloyd", "Potts", "Huber", "Nichols", "Bowen", "Colley",
			"Good", "Norton", "Oakley", "Gross", "Michael", "Snyder", "Nava", "Padilla", "Ellwood", "Lowe",
			"Dale", "Avila", "Hinton", "Cleveland", "Craig", "Rahman", "Bostock", "Mccall", "Knights",
			"Delaney", "Whitehead", "Weston", "Zamora", "Childs", "Walmsley", "Terrell", "Santos", "Burn",
			"Rayner", "Richards", "Schultz", "Arias", "Acevedo", "Page", "Laing", "Love", "Boyle", "Plummer",
			"Reed", "Duarte", "Hutchings", "Steele", "Brewer", "Fitzpatrick", "Shea", "Wells", "Dickinson",
			"Matthams", "Velasquez", "Barr", "Werner", "Villegas", "Winters", "Ridley", "Slater", "Snider",
			"Hamer", "Price");
	private final List<String> phoneNumbers = Arrays.asList("609-372-5639", "299-980-7253", "765-243-9102", "396-525-0068",
			"666-556-1956", "279-509-2647", "700-784-1406", "289-296-5399", "556-882-7010", "758-134-2171", "598-563-7726",
			"998-336-4318", "984-693-3422", "539-481-7143", "466-717-5151", "576-634-2102", "408-177-7765", "581-824-9711",
			"502-746-6397", "461-768-4385", "961-215-6970", "227-840-8525", "308-215-1468", "350-509-9176", "784-448-4751",
			"781-892-6694", "273-429-5712", "750-242-8249", "934-846-4993", "186-155-3936", "254-149-3093", "961-729-3195",
			"457-688-7112", "708-151-8078", "898-159-6077", "650-957-3927", "517-671-9202", "688-119-6262", "672-967-9574",
			"234-737-6148", "363-972-7665", "506-224-4899", "942-181-6034", "471-203-9299", "229-258-1818", "789-992-9157",
			"658-762-8845", "350-270-2528", "815-669-4650", "724-634-4010", "496-493-6210", "378-117-3337", "218-229-1221",
			"933-774-7693", "617-449-9167", "482-442-7664", "919-169-0760", "611-618-5153", "442-515-6777", "589-661-0775",
			"460-321-4810", "290-901-4330", "793-426-9425", "896-649-0311", "714-195-6564", "486-330-9544", "639-547-9846",
			"429-540-4407", "635-804-5758", "710-183-9112", "327-641-7562", "717-405-7537", "228-717-8091", "646-901-2690",
			"929-646-3357", "599-290-1605", "394-940-8892", "312-955-7096", "287-534-4150", "286-995-0736", "327-484-2815",
			"734-321-5412", "954-660-0653", "403-509-8961", "358-657-5211", "546-245-2245", "679-590-6924", "750-266-9149",
			"578-376-1934");
	private final List<String> domains = Arrays.asList("gmail.com", "yahoo.com", "aol.com", "iCloud.com", "outlook.com",
			"zoho.com", "msn.com", "live.com");
	private final List<String> addresses1 = Arrays.asList(
			"2 Oak Meadow Street", "7253 Talbot Ave.", "162 Surrey Lane", "77 S. Orchard Street", "23 Sutor Lane",
			"87 Glenholme Lane", "8754 Shadow Brook Court", "7299 Monroe Lane", "761 Smoky Hollow Ave.", "474 West 2nd St.",
			"9272 E. Deerfield Street", "8733 Foxrun Street", "7998 West Williams Dr.", "614 Cross Ave.", "8129 Park Drive",
			"69 College Rd.", "9191 East Linda St.", "819 Military Ave.", "37 Ashley Dr.", "7238 Baker Lane",
			"718 Hilltop Rd.", "7481 3rd Ave.", "30 Laurel Dr.", "815 Fairway Road", "266 Wentworth Dr.",
			"534 Myrtle Avenue", "8 Main Ave.", "36 Canterbury Rd.", "9717 Crescent Street", "7037 Railroad Drive",
			"17 High St.", "86 North Albany Ave.", "9295 W. South Avenue", "901 Illinois Drive", "7477 Santa Clara Street",
			"6 Eastlawn Circle", "4737 Forest Dale Avenue", "3954 Hansons Circle", "68312 Hudson Plaza", "9 Bashford Court",
			"423 Leroy Lane", "205 Almo Street", "137 Mcguire Parkway", "25187 Kropf Terrace", "0412 Pine View Parkway",
			"37 Schmedeman Court", "910 Mitchell Circle", "54993 Randy Park,", "562 Anniversary Park", "675 Heffernan Crossing",
			"4526 Alpine Lane", "12983 Arrowood Trail", "457 Eagan Park", "94 Golf View Trail,", "50584 Glacier Hill Parkway",
			"01 Darwin Center", "522 Melby Trail", "7395 Canary Crossing", "851 Bartelt Parkway", "1460 Texas Court",
			"24602 Farmco Parkway", "9 Manufacturers Drive", "44 Veith Pass", "65 Ohio Crossing", "2633 Bonner Road",
			"19 Bartillon Center", "7 Lotheville Hill", "117 Kipling Crossing", "7308 Fairview Trail", "8 Independence Plaza",
			"455 Stoughton Way", "875 Talmadge Avenue", "29473 East Hill", "68950 Towne Avenue", "17 Sherman Park", "12 Garrison Hill",
			"562 Fuller Circle", "30 Scoville Avenue", "9 Westerfield Terrace", "7 3rd Park", "74132 International Terrace",
			"04 Clemons Crossing", "8548 Oak Avenue", "807 New Castle Junction", "7 Graedel Trail", "0 Jana Terrace", "5 Darwin Lane",
			"7 Dapin Point", "4 Truax Pass", "38 Buhler Lane", "8986 Debs Court", "5 Montana Road", "5268 Westend Plaza",
			"05 Pleasure Drive", "24567 East Plaza", "62 Rusk Circle", "15 Oneill Point", "6413 Glacier Hill Court", "7 Nancy Lane",
			"5 Hayes Alley", "02 Karstens Street", "175 Sugar Park", "01708 Dunning Circle", "41371 Porter Alley", "0 Jenifer Court",
			"36737 Kinsman Road", "68 Heffernan Street", "44 Basil Circle", "5635 Division Point", "4 Cottonwood Junction",
			"6417 Arkansas Lane", "5 Dottie Junction", "85 Mesta Center", "0795 Londonderry Plaza", "84847 Carey Plaza",
			"7 Sunnyside Place", "5816 Stang Point", "2558 Toban Park", "90804 Debra Trail", "74 Vermont Street", "9509 Pleasure Parkway",
			"24388 Manley Parkway", "90916 Ronald Regan Court", "01891 Lukken Drive", "124 Waywood Pass", "0 Rutledge Junction",
			"95938 Butternut Center", "69 Cherokee Parkway", "6232 Oak Valley Pass", "7550 Debs Park", "9 Bellgrove Way",
			"99 Chive Parkway", "379 Fuller Drive", "1 East Lane", "0 Jenna Street");
	private final List<String> cities = Arrays.asList("Appleton", "Sunnyvale", "Indianapolis", "Mansfield", "Houston", "Spokane",
			"Wichita", "Detroit", "San Francisco", "New Orleans", "Irvine", "Dayton", "Seminole", "Norfolk", "Sacramento",
			"Salt Lake City", "Anniston", "Topeka", "Atlanta", "Anaheim", "Albany", "Riverside", "Sacramento", "Oakland",
			"Henderson", "Honolulu", "Oakland", "Oklahoma City", "Clearwater", "Daytona Beach", "Kansas City", "Washington",
			"Columbia", "Honolulu", "Corpus Christi", "Oakland", "Columbia", "Jackson", "Montgomery", "Odessa", "Miami",
			"San Antonio", "Wichita Falls", "Saint Paul", "Houston", "Knoxville", "Washington", "Troy", "Anniston", "Pittsburgh",
			"Indianapolis", "Phoenix", "Boston", "Wilmington", "Conroe", "Hartford", "Boston", "Little Rock", "Birmingham",
			"Gainesville", "Richmond", "Richmond", "Brockton", "New Bedford", "Wilkes Barre", "San Rafael", "Kansas City",
			"Gary", "New York City", "Portland", "Arlington", "Tacoma", "Garland", "Oakland", "Los Angeles", "Albuquerque",
			"Chattanooga", "Rochester", "Paterson", "Mesa", "Buffalo", "Washington", "Lincoln", "Indianapolis", "Huntington Beach",
			"Glendale", "Bellevue", "Honolulu", "El Paso", "Sacramento", "Atlanta", "San Francisco", "New Orleans", "Lexington",
			"Nashville", "Denton", "Harrisburg", "Washington", "Rochester", "Oklahoma City");
	private final List<String> states = Arrays.asList("WI", "CA", "IN", "OH", "TX", "WA", "KS", "MI", "LA", "FL", "VA", "UT", "AL",
			"GA", "HI", "OK", "NV", "MO", "DC", "SC", "MS", "MN", "TN", "PA", "AZ", "MA", "DE", "CT", "AR", "NY", "OR", "NM",
			"NJ", "NE", "KY");
	private final List<String> zipCodes = Arrays.asList("54915", "94089", "46247", "44905", "77228", "99220", "67236", "48232", "94177",
			"70187", "92717", "45470", "34642", "23520", "94237", "84110", "36205", "66611", "31106", "92812", "31704", "92505",
			"94263", "94660", "89074", "96835", "94616", "34615", "32123", "64160", "20442", "29220", "96810", "78426", "94622",
			"39210", "36114", "79769", "33158", "78278", "76305", "55123", "77245", "37931", "20557", "48098", "15235", "46239",
			"85072", "19897", "77305", "72209", "35295", "32627", "23293", "23285", "18706", "94913", "64136", "46406", "10120",
			"97271", "22205", "98411", "75049", "94616", "90010", "87105", "37405", "14604", "85210", "14205", "20337", "68524",
			"46254", "92648", "91205", "98008", "79950", "94291", "30368", "94164", "70183", "40546", "37210", "76210", "17126",
			"20566", "14652", "73173");

	/**
	 * Insert a customer record every 5 seconds if toggle is on.
	 * Toggle: application.yaml -> simulator-enabled
	 */
	@Scheduled(fixedRate = 5000)
	public void insertCustomer() {

		if (!simulatorEnabled) return;

		Random random = new Random();
		String firstName = firstNames.get(random.nextInt(firstNames.size()));
		String lastName = lastNames.get(random.nextInt(lastNames.size()));
		String domain = domains.get(random.nextInt(domains.size()));
		String email = String.format("%s.%s@%s", firstName.toLowerCase(), lastName.toLowerCase(), domain);

		CustomerDto customer = CustomerDto.builder()
				.firstName(firstName)
				.lastName(lastName)
				.phone(phoneNumbers.get(random.nextInt(phoneNumbers.size())))
				.email(email)
				.createdBy(createdBy)
				.createdDate(new Timestamp(System.currentTimeMillis()))
				.build();
		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(AddressDto.builder()
				.address1(addresses1.get(random.nextInt(addresses1.size())))
				.address2("")
				.city(cities.get(random.nextInt(cities.size())))
				.state(states.get(random.nextInt(states.size())))
				.zip(zipCodes.get(random.nextInt(zipCodes.size())))
				.createdBy(createdBy)
				.createdDate(new Timestamp(System.currentTimeMillis()))
				.addressType(AddressTypes.BILLING)
				.isDefault(true)
				.createdBy(createdBy)
				.createdDate(new Timestamp(System.currentTimeMillis()))
				.build());
		addresses.add(AddressDto.builder()
				.address1(addresses1.get(random.nextInt(addresses1.size())))
				.address2("")
				.city(cities.get(random.nextInt(cities.size())))
				.state(states.get(random.nextInt(states.size())))
				.zip(zipCodes.get(random.nextInt(zipCodes.size())))
				.createdBy(createdBy)
				.createdDate(new Timestamp(System.currentTimeMillis()))
				.addressType(AddressTypes.SHIPPING)
				.isDefault(false)
				.createdBy(createdBy)
				.createdDate(new Timestamp(System.currentTimeMillis()))
				.build());

		customerService.insert(EnrichedCustomer.builder()
				.customer(customer)
				.addresses(addresses)
				.build());
	}
}
