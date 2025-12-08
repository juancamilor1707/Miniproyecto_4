package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Componente visual de una celda individual del tablero.
 * Representa visualmente los diferentes estados: vacío, barco, impacto, fallo, hundido.
 * Maneja eventos de mouse para interacción del usuario y muestra previews de colocación.
 */
public class CellView extends StackPane {

    private final Rectangle background;
    private final double size;
    private String currentState;
    private boolean isPreviewActive;
    private String originalColor;

    /**
     * Constructor de la vista de celda.
     * @param size El tamaño en píxeles de la celda (ancho y alto)
     */
    public CellView(double size) {
        this.size = size;
        this.currentState = "EMPTY";
        this.isPreviewActive = false;

        background = new Rectangle(size, size);
        background.setFill(Color.web(Colors.WATER));
        background.setStroke(Color.web(Colors.GRID));
        background.setStrokeWidth(1);

        originalColor = Colors.WATER;

        getChildren().add(background);

        setOnMouseEntered(e -> {
            if (currentState.equals("EMPTY") && !isPreviewActive) {
                background.setFill(Color.web(Colors.HOVER));
            }
        });

        setOnMouseExited(e -> {
            if (currentState.equals("EMPTY") && !isPreviewActive) {
                background.setFill(Color.web(originalColor));
            }
        });
    }

    /**
     * Establece el color de fondo de la celda.
     * @param color El color en formato hexadecimal
     */
    public void setFill(String color) {
        background.setFill(Color.web(color));
        originalColor = color;
    }

    /**
     * Muestra un preview visual de dónde quedará el barco.
     * @param isValid true si la posición es válida (verde/oscuro), false si es inválida (rojo)
     */
    public void showPreview(boolean isValid) {
        if (currentState.equals("EMPTY")) {
            isPreviewActive = true;
            if (isValid) {
                // Preview válido - color verde oscuro/gris oscuro
                background.setFill(Color.web("#5A9F5A")); // Verde oscuro
                background.setOpacity(0.7);
            } else {
                // Preview inválido - color rojo
                background.setFill(Color.web("#E74C3C")); // Rojo
                background.setOpacity(0.5);
            }
        }
    }

    /**
     * Limpia el preview visual y restaura el color original.
     */
    public void clearPreview() {
        if (isPreviewActive && currentState.equals("EMPTY")) {
            isPreviewActive = false;
            background.setFill(Color.web(originalColor));
            background.setOpacity(1.0);
        }
    }

    /**
     * Marca la celda como impactada.
     * Cambia el color y agrega un marcador visual de impacto.
     */
    public void markAsHit() {
        currentState = "HIT";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.HIT));
        background.setOpacity(1.0);
        originalColor = Colors.HIT;

        Circle hitMarker = new Circle(size / 4);
        hitMarker.setFill(Color.web(Colors.HIT_MARKER));
        getChildren().add(hitMarker);
    }

    /**
     * Marca la celda como agua (disparo fallido).
     * Agrega una X como indicador visual.
     */
    public void markAsMiss() {
        currentState = "MISS";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.WATER));
        background.setOpacity(1.0);
        originalColor = Colors.WATER;

        Text missMarker = new Text("X");
        missMarker.setStyle("-fx-font-size: " + (size / 2) + "px; -fx-font-weight: bold;");
        missMarker.setFill(Color.web(Colors.MISS));
        getChildren().add(missMarker);
    }

    /**
     * Marca la celda como parte de un barco.
     * Cambia el color para mostrar la posición del barco.
     */
    public void markAsShip() {
        currentState = "SHIP";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.SHIP));
        background.setOpacity(1.0);
        originalColor = Colors.SHIP;
    }

    /**
     * Marca la celda como parte de un barco hundido.
     * Cambia el color y agrega marcador de hundido.
     */
    public void markAsSunk() {
        currentState = "SUNK";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.SUNK));
        background.setOpacity(1.0);
        originalColor = Colors.SUNK;

        Circle sunkMarker = new Circle(size / 3);
        sunkMarker.setFill(Color.web(Colors.SUNK_MARKER));
        getChildren().add(sunkMarker);
    }

    /**
     * Resetea la celda a su estado inicial vacío.
     */
    public void reset() {
        currentState = "EMPTY";
        isPreviewActive = false;
        getChildren().clear();
        getChildren().add(background);
        background.setFill(Color.web(Colors.WATER));
        background.setOpacity(1.0);
        originalColor = Colors.WATER;
    }

    /**
     * Refresca la visualización de la celda.
     */
    public void refresh() {
        // Método para compatibilidad, puede ser usado para actualizaciones futuras
    }

    /**
     * Obtiene el estado actual de la celda.
     * @return El estado actual ("EMPTY", "SHIP", "HIT", "MISS", "SUNK")
     */
    public String getCurrentState() {
        return currentState;
    }
}