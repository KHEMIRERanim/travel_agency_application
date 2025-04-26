package tests;

import entities.Client;
import services.ServiceClient;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import entities.Reclamation;
import services.ServiceReclamation;

public class Main {
    public static void main(String[] args) {
        ServiceClient clientService = new ServiceClient();
        Scanner scanner = new Scanner(System.in);
        ServiceReclamation reclamationService = new ServiceReclamation();
        try {
            // ===== 1. CLIENT ADDITION =====
            /*Client testClient = new Client(
                    "Smith",
                    "John",
                    "john.mortadha@gamail.com",
                    98765432,
                    "20/05/1985",
                    "password123"
            );

            if (!clientService.emailExists(testClient.getEmail())) {
                clientService.ajouter(testClient);
                System.out.println("Client added with ID: " + testClient.getId_client());
            } else {
                System.out.println("Client not added - email already exists!");
            }*/

            // ===== 2. CLIENT DELETION =====
            /*System.out.print("\nEnter client ID to delete: ");
            int clientId = scanner.nextInt();

            Client clientToDelete = new Client();
            clientToDelete.setId_client(clientId);

            clientService.supprimer(clientToDelete);
            System.out.println("Client " + clientId + " deleted!");*/

            // ===== 3. CLIENT MODIFICATION =====
            /*System.out.print("Enter client ID to modify: ");
            int clientId = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            Client existingClient = clientService.getById(clientId);
            if (existingClient == null) {
                System.out.println("Client not found!");
                return;
            }

            System.out.println("\nCurrent client data:");
            System.out.println("1. Last Name: " + existingClient.getNom());
            System.out.println("2. First Name: " + existingClient.getPrenom());
            System.out.println("3. Email: " + existingClient.getEmail());
            System.out.println("4. Phone: " + existingClient.getNumero_telephone());
            System.out.println("5. Birth Date: " + existingClient.getDate_de_naissance());
            System.out.println("6. Password");

            System.out.print("\nEnter field number to update (1-6): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            Client updatedClient = new Client(
                    existingClient.getNom(),
                    existingClient.getPrenom(),
                    existingClient.getEmail(),
                    existingClient.getNumero_telephone(),
                    existingClient.getDate_de_naissance(),
                    existingClient.getMot_de_passe()
            );
            updatedClient.setId_client(clientId);

            System.out.print("Enter new value: ");
            String newValue = scanner.nextLine();

            switch (choice) {
                case 1 -> updatedClient.setNom(newValue);
                case 2 -> updatedClient.setPrenom(newValue);
                case 3 -> {
                    if (clientService.emailExists(newValue) &&
                            !newValue.equals(existingClient.getEmail())) {
                        System.out.println("Email already in use!");
                        return;
                    }
                    updatedClient.setEmail(newValue);
                }
                case 4 -> {
                    try {
                        updatedClient.setNumero_telephone(Integer.parseInt(newValue));
                    } catch (NumberFormatException e) {
                        System.out.println("Phone must be a number!");
                        return;
                    }
                }
                case 5 -> updatedClient.setDate_de_naissance(newValue);
                case 6 -> updatedClient.setMot_de_passe(newValue);
                default -> {
                    System.out.println("Invalid choice!");
                    return;
                }
            }

            clientService.modifier(updatedClient);
            System.out.println("Client updated successfully!");*/

            // ===== 4. CLIENT LIST =====
           /* List<Client> clients = clientService.recuperer();

            System.out.println("\n=== CLIENT LIST ===");
            System.out.println("ID\tLast Name\tFirst Name\tEmail\t\tPhone\t\tBirth Date");
            System.out.println("------------------------------------------------------------");
            for (Client c : clients) {
                System.out.println(
                        c.getId_client() + "\t" +
                                c.getNom() + "\t\t" +
                                c.getPrenom() + "\t\t" +
                                c.getEmail() + "\t" +
                                c.getNumero_telephone() + "\t" +
                                c.getDate_de_naissance()
                );
            }
            System.out.println("\nTotal clients: " + clients.size());*/
            // Ready-made example data
            System.out.print("Enter Client ID for the reclamation: ");
            int clientId = scanner.nextInt();
            scanner.nextLine(); // consume newline         // Existing client ID from your database
            String type = "Complaint";
            String dateIncident = "15/05/2023";
            String description = "Product arrived damaged with broken packaging";


            System.out.println("=== ADDING SAMPLE RECLAMATION ===");
            System.out.println("Client ID: " + clientId);
            System.out.println("Type: " + type);
            System.out.println("Date: " + dateIncident);
            System.out.println("Description: " + description);

            Reclamation sampleReclamation = new Reclamation(
                    clientId,
                    type,
                    dateIncident,
                    description
            );

            reclamationService.ajouter(sampleReclamation);
            System.out.println("\n✅ Reclamation added successfully!");
            System.out.println("Generated ID: " + sampleReclamation.getId_reclamation());

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}