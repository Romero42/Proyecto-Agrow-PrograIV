package cr.ac.una.agrow.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static final Logger LOG = Logger.getLogger(Util.class.getName());

    /**
     * Helper simple para dividir el mensaje de resultado ("tipo,mensaje").
     * Centralizado para evitar duplicación.
     * @param datos String en formato "tipo,mensaje".
     * @return String[] con [tipo, mensaje] o un array de error si el formato es inválido.
     */
    public static String[] arraySplit(String datos) {
        if (datos == null) {
            LOG.log(Level.WARNING, "arraySplit recibió datos nulos.");
            // Devolver un error genérico consistente con el formato esperado (tipo 2 = error)
            return new String[]{"2", "Error interno: Mensaje de respuesta nulo."};
        }
        // Divide solo por la primera coma para separar tipo y mensaje
        String[] parts = datos.split(",", 2);
        if (parts.length == 2) {
            // Validar que el primer elemento sea un número (1 o 2)
            try {
                int type = Integer.parseInt(parts[0]);
                if (type == 1 || type == 2) {
                    return parts;
                } else {
                    LOG.log(Level.WARNING, "arraySplit recibió tipo inesperado: {0} en datos: {1}", new Object[]{parts[0], datos});
                     return new String[]{"2", "Error interno: Tipo de mensaje inesperado (" + parts[0] + ")." };
                }
            } catch (NumberFormatException e) {
                 LOG.log(Level.WARNING, "arraySplit no pudo parsear el tipo como número: {0} en datos: {1}", new Object[]{parts[0], datos});
                 return new String[]{"2", "Error interno: Formato de tipo de mensaje inválido (" + parts[0] + ")." };
            }
        } else {
             // Si no se pudo dividir en 2 partes
             LOG.log(Level.WARNING, "arraySplit recibió formato inesperado (no se pudo dividir): {0}", datos);
             // Devolver un mensaje de error útil si es posible, o uno genérico
             return new String[]{"2", "Error interno: Formato de mensaje inesperado."};
        }
    }
}