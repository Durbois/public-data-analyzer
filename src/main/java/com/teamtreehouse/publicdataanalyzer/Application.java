package com.teamtreehouse.publicdataanalyzer;

import com.teamtreehouse.publicdataanalyzer.model.Country;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Application {

  private static final SessionFactory sessionFactory = buildSessionFactory();

  private static SessionFactory buildSessionFactory(){

    // Create standard service registry object
    final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
    return new MetadataSources(registry).buildMetadata().buildSessionFactory();
  }

  public static void main(String[]args){

    String choice = "";

    Scanner scanner = new Scanner(System.in);

    while (!choice.equals("quit")) {

      System.out.println("------------------------------------------");
      System.out.println("Type list to view a list of well formatted data for all countries");
      System.out.println("Type stats to view Statistics");
      System.out.println("Type edit to edit a country");
      System.out.println("Type add to add a country");
      System.out.println("Type delete to delete a selected country");
      System.out.println("Type quit to exit the program");
      System.out.println("------------------------------------------");

      choice = scanner.nextLine();

      switch (choice) {
        case "list":
          viewAllCountries();
          break;

        case "stats":
          findStatistics();
          break;

        case "edit":
          editCountry(findAllCountries());
          break;

        case "add":
          addCountry(findAllCountries());
          break;

        case "delete":
          deleteCountry(findAllCountries());
          break;
      }
    }
  }

  public static void viewAllCountries () {

    List<Country> countries = findAllCountries();

    String specifiers = "%-35s %-30s %-40s%n";
    System.out.format(specifiers, "Country", "Internet Users", "Literacy");
    System.out.format("---------------------------------------------------------------------------%n");

    DecimalFormat df = new DecimalFormat("#.00");

    countries.stream()
        .forEach(i -> System.out.format(specifiers,
            i.getName(),
            (i.getInternetUsers() != null ? "" + df.format(i.getInternetUsers()) : "--"),
            (i.getAdultLiteracyRate() != null ? "" + df.format(i.getAdultLiteracyRate()) : "--"))
        );

  }

  public static List<Country> findAllCountries () {
    Session session = sessionFactory.openSession();

    List<Country> countries = session.createQuery("FROM Country")

        .getResultList();

    session.close();

    return countries;
  }

  public static void findStatistics () {
    findCorrelationCoefficient();
    findMinMaxValueForEachIndicator();
  }

  private static void findMinMaxValueForEachIndicator() {
    Country countryWithMinInternetUsers = findAllCountries().stream()
        .filter(c -> c.getInternetUsers() != null)
        .min(Comparator.comparingDouble(c -> c.getInternetUsers())).get();

    Country countryWithMaxInternetUsers = findAllCountries().stream()
        .filter(c -> c.getInternetUsers() != null)
        .max(Comparator.comparingDouble(c -> c.getInternetUsers())).get();

    Country countryWithMinLitteracyRate = findAllCountries().stream()
        .filter(c -> c.getAdultLiteracyRate() != null)
        .min(Comparator.comparingDouble(c -> c.getAdultLiteracyRate())).get();

    Country countryWithMaxLitteracyRate = findAllCountries().stream()
        .filter(c -> c.getAdultLiteracyRate() != null)
        .max(Comparator.comparingDouble(c -> c.getAdultLiteracyRate())).get();

    System.out.printf("Country with min Internet Users is %s with a value of %f%n",
        countryWithMinInternetUsers.getName(), countryWithMinInternetUsers.getInternetUsers() );

    System.out.printf("Country with max Internet Users is %s with a value of %f%n",
        countryWithMaxInternetUsers.getName(), countryWithMaxInternetUsers.getInternetUsers() );

    System.out.printf("Country with min Literacy rate is %s with a value of %f%n",
        countryWithMinLitteracyRate.getName(), countryWithMinLitteracyRate.getAdultLiteracyRate() );

    System.out.printf("Country with max Literacy rate is %s with a value of %f%n",
        countryWithMaxLitteracyRate.getName(), countryWithMaxLitteracyRate.getAdultLiteracyRate() );
  }

  private static void findCorrelationCoefficient() {

    List<Country> countries = findAllCountries().stream()
        .filter(c -> c.getInternetUsers() != null)
        .filter(c -> c.getAdultLiteracyRate() != null)
        .collect(Collectors.toList());

    double[] internetUsage = new double[countries.size()];
    double[] literacyRate = new double[countries.size()];

    IntStream.rangeClosed(0, (countries.size() - 1))
        .forEach(i -> {
          internetUsage[i] = countries.get(i).getInternetUsers();
          literacyRate[i] = countries.get(i).getAdultLiteracyRate();
        });

    double corr = new PearsonsCorrelation().correlation(internetUsage, literacyRate);

    System.out.printf("Correlation Coefficient for the two indicators = %f%n", corr);
  }

  public static void editCountry ( List<Country> countries ) {

    while(true) {
      Scanner scanner = new Scanner(System.in);

      System.out.println("Type a country you want to edit (please type the whole name as in the List of country: see the menu)");
      System.out.println("Type quit to exit");
      String name = scanner.nextLine();

      if ( name.equalsIgnoreCase("quit")) {
        break;
      }

      Country country = countries.stream()
          .filter(c -> c.getName().equalsIgnoreCase(name))
          .findFirst()
          .orElse(null);

      if(country != null){
        System.out.printf("To edit country is %s%n", country);

        System.out.println("Which property of the country do you want to update? ");
        System.out.println("Enter 1 for Internet Users, Enter 2 for Literacy");

        int choice = scanner.nextInt();

        switch (choice) {
          case 1:
            Session session = sessionFactory.openSession();

            session.beginTransaction();
            System.out.println("Enter the new internet usage:");
            double newInternetUsage = scanner.nextDouble();
          int updatedCountry = session.createQuery(
              "update Country " +
                  "set INTERNETUSERS = " + newInternetUsage +
                  "where name = '" + country.getName() + "'" )
              .executeUpdate();

            session.getTransaction().commit();
            session.close();
            break;

          case 2:
            session = sessionFactory.openSession();
            session.beginTransaction();
            System.out.println("Enter the new adult literacy rate:");
            double adultLiteracy = scanner.nextDouble();
            updatedCountry = session.createQuery(
                "update Country " +
                    "set ADULTLITERACYRATE = " + adultLiteracy +
                    "where name = '" + country.getName() + "'" )
                .executeUpdate();

            session.getTransaction().commit();
            session.close();
            break;

          default:
            System.out.println("Please select a number between 1 and 2");
            break;
        }

        country = findAllCountries().stream()
                                   .filter(c -> c.getName().equalsIgnoreCase(name))
                                   .findFirst()
                                   .get();
        System.out.printf("The edited country is %s%n", country);
      } else {
        System.out.printf("Country with the name %s doesn't exist%n", name);
        System.out.println("Choose another one.");
      }
    }
  }

  public static void addCountry ( List<Country> countries ) {

    while(true) {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Type the country name you want to add to the Database");
      System.out.println("Type quit to exit");
      String name = scanner.nextLine();

      if ( name.equalsIgnoreCase("quit")) {
        break;
      }

      Country country = countries.stream()
          .filter(c -> c.getName().equalsIgnoreCase(name))
          .findFirst()
          .orElse(null);

      if (country != null ) {
        System.out.printf("%s already exist%n", country.getName());
      } else {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.println("Enter Code:");
        String code = scanner.nextLine();

        System.out.println("Enter internetUsers:");
        Double internetUsers = scanner.nextDouble();

        System.out.println("Enter AdultLiteracy:");
        Double adultLiteracy = scanner.nextDouble();

        country = new Country.CountryBuilder(code, name)
           .withInternetUser(internetUsers)
           .withAdultLiteracy(adultLiteracy)
           .build();

        session.saveOrUpdate(country);

        session.getTransaction().commit();
        session.close();
        System.out.println(country.getName() + " saved!");
      }
    }
  }

  public static void deleteCountry ( List<Country> countries ) {
    while(true) {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Type the country name you want to delete from the Database");
      System.out.println("Type quit to exit");
      String name = scanner.nextLine();

      if ( name.equalsIgnoreCase("quit")) {
        break;
      }

      Country country = countries.stream()
          .filter(c -> c.getName().equalsIgnoreCase(name))
          .findFirst()
          .orElse(null);

      if ( country != null ) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(country);

        session.getTransaction().commit();
        session.close();
        System.out.println(country.getName() + " deleted!");
      } else {
        System.out.println("Country doesn't exist");
      }
    }
  }
}
