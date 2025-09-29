package com.unicauca.proyectosofv1.ui.util;

import java.util.Arrays;

public final class ValidadorPassword {

    public static final class Resultado {
        public final boolean hasLen, hasUpper, hasLower, hasDigit, hasSymbol;
        public final int score;   // 0..5
        public final boolean ok;  // cumple la política

        private Resultado(boolean len, boolean up, boolean lo, boolean di, boolean sy) {
            this.hasLen=len; this.hasUpper=up; this.hasLower=lo; this.hasDigit=di; this.hasSymbol=sy;
            int s = 0; if (len) s++; if (up) s++; if (lo) s++; if (di) s++; if (sy) s++;
            this.score = s;
            this.ok = len && up && lo && di && sy; // exige TODOS (ajústalo si la minúscula no fuera obligatoria)
        }
    }

    public static Resultado evaluar(char[] pwd) {
        if (pwd == null) pwd = new char[0];
        boolean len = pwd.length >= 6;
        boolean up=false, lo=false, di=false, sy=false;
        for (char c : pwd) {
            if      (Character.isUpperCase(c)) up = true;
            else if (Character.isLowerCase(c)) lo = true;
            else if (Character.isDigit(c))     di = true;
            else                                sy = true;
        }
        Resultado r = new Resultado(len, up, lo, di, sy);
        Arrays.fill(pwd, '\0');
        return r;
    }

    /** Mini-checklist en HTML para poner directo en un JLabel */
    public static String htmlChecklist(Resultado r) {
        return """
        <html>
           <div style='font-family:Segoe UI, sans-serif; font-size:11pt'>
             <b>Contraseña:</b>
             <ul style='margin:4 0 0 16'>
               <li>%s 6+ caracteres</li>
               <li>%s 1 mayúscula (A-Z)</li>
               <li>%s 1 minúscula (a-z)</li>
               <li>%s 1 dígito (0-9)</li>
               <li>%s 1 símbolo (!@#$...)</li>
             </ul>
           </div>
        </html>
        """.formatted(
                mark(r.hasLen),
                mark(r.hasUpper),
                mark(r.hasLower),
                mark(r.hasDigit),
                mark(r.hasSymbol)
        );
    }

    private static String mark(boolean ok) {
        return ok ? "<span style='color:#2e7d32'>&#10003;</span>"
                  : "<span style='color:#c62828'>&#10007;</span>";
    }
}
