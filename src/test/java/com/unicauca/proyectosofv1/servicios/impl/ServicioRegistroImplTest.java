/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.unicauca.proyectosofv1.servicios.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author JUANCAMILOMENESESDAZ
 */
public class ServicioRegistroImplTest {
    
    public ServicioRegistroImplTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
    }

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() throws Exception {
    }
    
   

    @org.junit.jupiter.api.Test
    public void testRegistrar() throws Exception {
        System.out.println("registrar");
        String nombres = "";
        String apellidos = "";
        String celular = "";
        String programa = "";
        String rol = "";
        String email = "";
        String contraseniaPlano = "";
        ServicioRegistroImpl instance = null;
        instance.registrar(nombres, apellidos, celular, programa, rol, email, contraseniaPlano);
        fail("The test case is a prototype.");
    }
    
}
