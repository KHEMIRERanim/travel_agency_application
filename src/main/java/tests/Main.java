package tests;

import entities.Client;
import entities.ReservationVehicule;
import entities.Vehicule;
import services.ServiceClient;
import services.ServiceReservationVehicule;
import services.ServiceVehicule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        testClientCRUD();
        testVehiculeCRUD();
        testReservationVehiculeCRUD();
    }

    private static void testClientCRUD() {
        ServiceClient service = new ServiceClient();
        try {
            // Create
            System.out.println("Testing CREATE client...");
            Client newClient = new Client(0, "John", "Doe", "john.doe@example.com", 123456789, null, "Tunis");
            service.ajouter(newClient);
            Integer clientId = service.getClientIdByEmail("john.doe@example.com");
            if (clientId != null) {
                System.out.println("Client created: Email=" + newClient.getEmail() + ", ID=" + clientId);
            } else {
                throw new SQLException("Failed to retrieve client ID after creation");
            }

            // Read
            System.out.println("Testing READ clients...");
            List<Client> clients = service.recuperer();
           // clients.forEach(c -> System.out.println("Client: ID=" + c.getIdClient() + ", Email=" + c.getEmail() + ", Name=" + c.getNom() + " " + c.getPrenom()));

            // Update
            if (!clients.isEmpty()) {
                System.out.println("Testing UPDATE client...");
                Client toUpdate = clients.stream()
                        .filter(c -> "john.doe@example.com".equals(c.getEmail()))
                        .findFirst()
                        .orElse(null);
                if (toUpdate != null) {
                    toUpdate.setNom("Jane");
                    toUpdate.setPrenom("Doe");
                    service.modifier(toUpdate);
                  //  System.out.println("Client updated: ID=" + toUpdate.getIdClient());
                }
            }

            // Delete
            if (!clients.isEmpty()) {
                System.out.println("Testing DELETE client...");
                Client toDelete = clients.stream()
                        .filter(c -> "john.doe@example.com".equals(c.getEmail()))
                        .findFirst()
                        .orElse(null);
                if (toDelete != null) {
                  //  service.supprimer(toDelete.getIdClient());
                   // System.out.println("Client deleted: ID=" + toDelete.getIdClient());
                }
            }

            // Read again
            System.out.println("Testing READ clients after deletion...");
            clients = service.recuperer();
           // clients.forEach(c -> System.out.println("Client: ID=" + c.getIdClient() + ", Email=" + c.getEmail() + ", Name=" + c.getNom() + " " + c.getPrenom()));

        } catch (SQLException e) {
            System.out.println("Error during Client CRUD test: " + e.getMessage());
        }
    }

    private static void testVehiculeCRUD() {
        ServiceVehicule service = new ServiceVehicule();
        try {
            // Create
            System.out.println("Testing CREATE vehicle...");
            Vehicule newVehicule = new Vehicule(0, "Sedan", "Gabes", "Tunis", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5), "sedan.jpg", 80.00);
            service.ajouter(newVehicule);
            System.out.println("Vehicle created: " + newVehicule.getType());

            // Read
            System.out.println("Testing READ vehicles...");
            List<Vehicule> vehicules = service.getAll();
            if (vehicules.isEmpty()) {
                System.out.println("No vehicles found.");
            } else {
                vehicules.forEach(v -> System.out.println("Vehicle: ID=" + v.getId() + ", Type=" + v.getType() + ", Prix=" + v.getPrix()));
            }

            // Update
            if (!vehicules.isEmpty()) {
                System.out.println("Testing UPDATE vehicle...");
                Vehicule toUpdate = vehicules.get(vehicules.size() - 1);
                toUpdate.setType("Updated Sedan");
                toUpdate.setPrix(90.00);
                service.modifier(toUpdate);
                System.out.println("Vehicle updated: ID=" + toUpdate.getId());
            }

            // Delete
            if (!vehicules.isEmpty()) {
                System.out.println("Testing DELETE vehicle...");
                int idToDelete = vehicules.get(vehicules.size() - 1).getId();
                service.supprimer(idToDelete);
                System.out.println("Vehicle deleted: ID=" + idToDelete);
            }

            // Read again
            System.out.println("Testing READ vehicles after deletion...");
            vehicules = service.getAll();
            if (vehicules.isEmpty()) {
                System.out.println("No vehicles found.");
            } else {
                vehicules.forEach(v -> System.out.println("Vehicle: ID=" + v.getId() + ", Type=" + v.getType() + ", Prix=" + v.getPrix()));
            }

        } catch (SQLException e) {
            System.out.println("Error during Vehicule CRUD test: " + e.getMessage());
        }
    }

    private static void testReservationVehiculeCRUD() {
        ServiceReservationVehicule service = new ServiceReservationVehicule();
        ServiceVehicule vehiculeService = new ServiceVehicule();
        ServiceClient clientService = new ServiceClient();
        try {
            // Create a test client
            System.out.println("Creating test client for reservation...");
            Client testClient = new Client(0, "Test", "User", "test.user@example.com", 987654321, null, "Sousse");
            clientService.ajouter(testClient);
            Integer clientId = clientService.getClientIdByEmail("test.user@example.com");
            if (clientId == null) {
                System.out.println("Failed to create test client.");
                return;
            }
            System.out.println("Test client created: ID=" + clientId);

            // Get a vehicle ID
            List<Vehicule> vehicules = vehiculeService.getAll();
            if (vehicules.isEmpty()) {
                System.out.println("No vehicles available for reservation test.");
                return;
            }
            int vehicleId = vehicules.get(0).getId();

            // Create
            System.out.println("Testing CREATE reservation...");
            ReservationVehicule newReservation = new ReservationVehicule(
                    0, vehicleId, clientId, "Tunis", "Tunis",
                    LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5)
            );
            newReservation.setClientEmail("test.user@example.com");
            service.ajouter(newReservation);
            System.out.println("Reservation created for vehicle ID: " + vehicleId + ", Client Email: " + newReservation.getClientEmail());

            // Read
            System.out.println("Testing READ reservations...");
            List<ReservationVehicule> reservations = service.getAll();
            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                reservations.forEach(r -> System.out.println(
                        "Reservation: ID=" + r.getId() + ", Vehicle ID=" + r.getId_vehicule() +
                                ", Client Email=" + r.getClientEmail() + ", Pickup=" + r.getLieu_prise()
                ));
            }

            // Update
            if (!reservations.isEmpty()) {
                System.out.println("Testing UPDATE reservation...");
                ReservationVehicule toUpdate = reservations.get(reservations.size() - 1);
                toUpdate.setLieu_prise("Sousse");
                toUpdate.setLieu_retour("Sousse");
                service.modifier(toUpdate);
                System.out.println("Reservation updated: ID=" + toUpdate.getId());
            }

            // Delete
            if (!reservations.isEmpty()) {
                System.out.println("Testing DELETE reservation...");
                int idToDelete = reservations.get(reservations.size() - 1).getId();
                service.supprimer(idToDelete);
                System.out.println("Reservation deleted: ID=" + idToDelete);
            }

            // Read again
            System.out.println("Testing READ reservations after deletion...");
            reservations = service.getAll();
            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                reservations.forEach(r -> System.out.println(
                        "Reservation: ID=" + r.getId() + ", Vehicle ID=" + r.getId_vehicule() +
                                ", Client Email=" + r.getClientEmail() + ", Pickup=" + r.getLieu_prise()
                ));
            }

            // Clean up test client
            System.out.println("Cleaning up test client...");
          //  clientService.supprimer(clientId);
            System.out.println("Test client deleted: ID=" + clientId);

        } catch (SQLException e) {
            System.out.println("Error during ReservationVehicule CRUD test: " + e.getMessage());
        }
    }
}