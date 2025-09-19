package com.unicauca.proyectosofv1.seguridad;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncriptadorContrasenia {
    private static final int ITERACIONES = 210_000;
    private static final int TAM_LLAVE_BITS = 256;
    private static final int TAM_SAL = 16;

    public String generarHash(char[] contrasenia) {
        try {
            byte[] sal = new byte[TAM_SAL];
            new SecureRandom().nextBytes(sal);

            KeySpec spec = new PBEKeySpec(contrasenia, sal, ITERACIONES, TAM_LLAVE_BITS);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();

            return "pbkdf2$" + ITERACIONES + "$" +
                    Base64.getEncoder().encodeToString(sal) + "$" +
                    Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generando hash de contraseña", e);
        }
    }

    public boolean verificar(char[] contrasenia, String hashGuardado) {
        try {
            String[] partes = hashGuardado.split("\\$");
            if (partes.length != 4 || !partes[0].equals("pbkdf2")) return false;
            int it = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);

            KeySpec spec = new PBEKeySpec(contrasenia, sal, it, hashEsperado.length * 8);
            byte[] hashCalc = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();

            if (hashCalc.length != hashEsperado.length) return false;
            int diff = 0;
            for (int i = 0; i < hashCalc.length; i++) diff |= hashCalc[i] ^ hashEsperado[i];
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
