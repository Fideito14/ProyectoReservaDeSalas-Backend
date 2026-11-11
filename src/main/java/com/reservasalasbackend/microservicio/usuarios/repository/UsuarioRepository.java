package com.reservasalasbackend.microservicio.usuarios.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.reservasalasbackend.microservicio.usuarios.model.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@RequiredArgsConstructor
public class UsuarioRepository {

    private final Firestore firestore;

    public Optional<Usuario> buscarPorUid(String uid)
            throws InterruptedException, ExecutionException {

        DocumentSnapshot documento = firestore
                .collection("usuarios")
                .document(uid)
                .get()
                .get();

        if (!documento.exists()) {
            return Optional.empty();
        }

        Usuario usuario = documento.toObject(Usuario.class);

        if (usuario != null) {
            usuario.setUid(documento.getId());
        }

        return Optional.ofNullable(usuario);
    }

}
