
package com.reservasalasbackend.microservicio;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseTest {

    @Bean
    public CommandLineRunner testFirebase(Firestore firestore) {
        return args -> {

            System.out.println("Consultando Firestore...");

            QuerySnapshot snapshot = firestore
                    .collection("salas")
                    .limit(1)
                    .get()
                    .get();

            System.out.println("==================================");
            System.out.println("FIREBASE CONECTADO CORRECTAMENTE");
            System.out.println("Consulta realizada correctamente.");
            System.out.println("Documentos encontrados: "
                    + snapshot.size());
            System.out.println("==================================");
        };
    }
}
