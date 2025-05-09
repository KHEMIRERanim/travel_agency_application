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

public class Main{
    public static void main(String[] args) {
        testVehiculeCRUD();
        testReservationVehiculeCRUD();

    }

    private static void testVehiculeCRUD() {
        ServiceVehicule service = new ServiceVehicule();
        try {
            // Create
            System.out.println("Testing CREATE vehicle...");
            Vehicule newVehicule = new Vehicule(0, "Sedan", "gabes", "Tunis", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5), "images/sedan.jpg", 80.00);
            service.ajouter(newVehicule);
            System.out.println("Vehicle created: " + newVehicule.getType());

            // Read
            System.out.println("Testing READ vehicles...");
            List<Vehicule> vehicules = service.getAll();
            vehicules.forEach(v -> System.out.println("Vehicle: ID=" + v.getId() + ", Type=" + v.getType() + ", Prix=" + v.getPrix()));

            // Update
            if (!vehicules.isEmpty()) {
                System.out.println("Testing UPDATE vehicle...");
                Vehicule toUpdate = vehicules.get(vehicules.size() - 1); // Update the last added vehicle
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
            vehicules.forEach(v -> System.out.println("Vehicle: ID=" + v.getId() + ", Type=" + v.getType() + ", Prix=" + v.getPrix()));

        } catch (SQLException e) {
            System.out.println("Error during Vehicule CRUD test: " + e.getMessage());
        }
    }

    private static void testReservationVehiculeCRUD() {
        ServiceReservationVehicule service = new ServiceReservationVehicule();
        ServiceVehicule vehiculeService = new ServiceVehicule();
        try {
            // Get a vehicle ID for reservation
            List<Vehicule> vehicules = vehiculeService.getAll();
            if (vehicules.isEmpty()) {
                System.out.println("No vehicles available for reservation test.");
                return;
            }
            int vehicleId = vehicules.get(0).getId();
            Integer clientId = 1; // Assuming client ID 1 exists

            // Create
            System.out.println("Testing CREATE reservation...");
            ReservationVehicule newReservation = new ReservationVehicule(0, vehicleId, clientId, "Tunis", "Tunis", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5));
            service.ajouter(newReservation);
            System.out.println("Reservation created for vehicle ID: " + vehicleId);

            // Read
            System.out.println("Testing READ reservations...");
            List<ReservationVehicule> reservations = service.getAll();
            reservations.forEach(r -> System.out.println("Reservation: ID=" + r.getId() + ", Vehicle ID=" + r.getIdVehicule() + ", Client ID=" + r.getIdClient()));

            // Update
            if (!reservations.isEmpty()) {
                System.out.println("Testing UPDATE reservation...");
                ReservationVehicule toUpdate = reservations.get(reservations.size() - 1); // Update the last added reservation
                toUpdate.setLieuPrise("Sousse");
                toUpdate.setLieuRetour("Sousse");
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
            reservations.forEach(r -> System.out.println("Reservation: ID=" + r.getId() + ", Vehicle ID=" + r.getIdVehicule() + ", Client ID=" + r.getIdClient()));

        } catch (SQLException e) {
            System.out.println("Error during ReservationVehicule CRUD test: " + e.getMessage());
        }
    }


}