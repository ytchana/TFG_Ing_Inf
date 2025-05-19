package com.example.walkindoor.sensorsManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Esta clase administra la detección de pasos y la orientación del usuario (dispositivo).
 * Hay que recalcar que la orientación que toma es la del dispositivo, y no la del usuario.
 * Si el usuario va hacia el norte pero gira su dispositivo hacia el sur, el sistema diburá los pasos hacia el sur.
 * Usa un combinado de filtro de paso bajo y de Kalman para reducir el ruido en los valores del acelerómetro.
 *
 * Los datos de esta clase se recuperan después desde la clase CustomMapView, donde se maneja la vista.
 */
public class SensorHandler implements SensorEventListener {

    // Gestor de sensores del dispositivo
    private final SensorManager sensorManager;

    // Sensores utilizados
    private final Sensor rotationSensor;      // Sensor de rotación (rumbo preciso)
    private final Sensor accelerometerSensor; // Sensor de aceleración (detección de pasos con el acelerómetro)
    private Sensor stepSensor; // Sensor de aceleración (detección de pasos)
    private final Sensor magnetometerSensor;  // Sensor magnético (para calcular rumbo sin sensor de rotación)


    // Variables para datos del sensor
    private float[] gravity;      // Datos del acelerómetro
    private float[] geomagnetic;  // Datos del magnetómetro
    private float azimuth = 0;    // Dirección del dispositivo en grados
    private float accelerationZ;  // Aceleración en el eje Z
    float[] rotationMatrix = new float[9];
    float[] orientationValues = new float[3];

    // Control de tiempo entre pasos
    private long lastStepTime = 0;
    private static final long STEP_DELAY = 800; // Tiempo mínimo entre pasos en milisegundos

    // Parámetros para el filtro de paso bajo
    private static final float STEP_THRESHOLD = 3.0f; // Umbral para detectar un paso válido
    private static final float ALPHA = 0.85f; // Factor de suavizado del filtro de paso bajo

    private float lastAcceleration = 0; // Última aceleración registrada
    private static final float MIN_ACCELERATION_CHANGE = 1.0f; // Diferencia mínima para validar un paso

    // Callback para comunicarse con otras clases
    private final SensorCallback callback;

    /**
     * Constructor del `SensorHandler`.
     * Configura los sensores disponibles y verifica si el dispositivo tiene los necesarios.
     *
     * @param context  Contexto de la aplicación.
     * @param callback Callback para enviar actualizaciones de rumbo y pasos.
     */
    public SensorHandler(Context context, SensorCallback callback) {
        this.callback = callback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        } else {
            throw new RuntimeException("Error: SensorManager no disponible.");
        }
    }

    /**
     * Registra los sensores para recibir eventos de cambio de valores.
     * Nota importante: No todos los dispositivos poseen todos los sensores
     * Aquí se ha optado primero por el sensor de rotación, que po sí solo ofrece el resultado más preciso en la orientación.
     * Pero en caso de que el dispositivo no dispusiera del sensor de rotación, pues el cálculo de rotación se haría combinando el acelerómetro y el magnetómetro.
     */
    public void registerSensors() {

        if (sensorManager == null) return;

        // Registrar sensor de rotación
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
            System.out.println("Sensor de rotación registrado correctamente.");
        } else {
            System.out.println("Advertencia: Sensor de rotación no disponible, usando acelerómetro/magnetómetro.");
        }
        // Registrar sensor de pasos
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        // Registrar acelerómetro para calcular el rumbo
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            System.out.println("Acelerómetro registrado correctamente.");
        } else {
            System.out.println("Error: Acelerómetro no disponible.");
        }
        // Registrar magnetómetro para calcular el rumbo
        if (magnetometerSensor != null) {
            sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
            System.out.println("Magnetómetro registrado correctamente.");
        } else {
            System.out.println("Advertencia: Magnetómetro no disponible, la orientación puede no ser precisa.");
        }
    }

    /**
     * Desregistra los sensores para evitar consumo innecesario de recursos.
     */
    public void unregisterSensors() {
        sensorManager.unregisterListener(this);
    }

    /**
     * Aquí se procesan los datos sensoriales registrados.
     * Es decir, cada vez que cambia el valor de uno de los sensores activados, este método se encarga de recuperar dicho valor.
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Manejo del sensor de rotación (forma más precisa de calcular el rumbo)
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            azimuth = (float) Math.toDegrees(orientationValues[0]);
            if (azimuth < 0) azimuth += 360;

            callback.onAzimuthUpdated(azimuth);
            System.out.println("Rumbo actualizado con sensor de rotación: " + azimuth);
        }

        // Manejo del acelerómetro: se aplica el filtro combinado para mejorar la detección de pasos
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // Aplicación del filtro combinado al sensor de aceleración
            gravity = applyCombinedFilter(event.values, gravity, 0.005f, 0.1f); // Suaviza los datos del acelerómetro
            // Obtención de la aceleración en el eje Z
            accelerationZ = gravity[2];

            // Obtención del tiempo actual
            long currentTime = System.currentTimeMillis();
            // Cálculo del cambio de aceleración para validar si se ha producido un paso
            float accelerationChange = Math.abs(accelerationZ - lastAcceleration);

            // Calcular la diferencia de aceleración para validar un paso
            // Si el cambio de aceleración es suficiente y supera el umbral de detección de paso
            if (accelerationChange > MIN_ACCELERATION_CHANGE && Math.abs(accelerationZ) > STEP_THRESHOLD) {
                // Verificación de que ha pasado suficiente tiempo desde el último paso detectado
                if (currentTime - lastStepTime > STEP_DELAY) {
                    lastStepTime = currentTime; // Se actualiza el tiempo del último paso
                    lastAcceleration = accelerationZ; // Se guarda la última aceleración medida
                    callback.onStepDetected(); // Se notifica la detección de un nuevo paso
                    System.out.println("Paso detectado.");
                }
            }
        }

        // Manejo del magnetómetro (usado junto con el acelerómetro para calcular el rumbo)
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        // Calcular rumbo usando acelerómetro y magnetómetro si no hay sensor de rotación
        if (gravity != null && geomagnetic != null && rotationSensor == null) {

            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                SensorManager.getOrientation(rotationMatrix, orientationValues);

                azimuth = (float) Math.toDegrees(orientationValues[0]);
                if (azimuth < 0) azimuth += 360;

                callback.onAzimuthUpdated(azimuth);
                System.out.println("Rumbo calculado con acelerómetro/magnetómetro: " + azimuth);
            }
        }
    }

    /**
     * -> Enlace a fuente consultada:
     *      MAMN01_MySensorApp: https://github.com/jhelbrink/MAMN01_MySensorApp/blob/765e305fef25221e988f93e63ba684f3ff87e319/MySensorApp/app/src/main/java/com/example/john/mysensorapp/DisplayCompassActivity.java
     *
     * -> https://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
     */



    /**
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *         {@code SensorManager.SENSOR_STATUS_*}
     */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar aquí
    }

    /**
     * Aplica un filtro de paso bajo para suavizar los valores del acelerómetro.
     *
     * @param input  Valores actuales del acelerómetro.
     * @param output Valores suavizados previos.
     * @return Valores suavizados.
     */

    /** Este método combina un filtro paso bajo y un filtro de Kalman para mejorar la precisión de los datos
     * Este método es el nervio de todo este sistema.
     * Hay que tener en cuenta que los valores de los sensores cambian muy de prisa, por lo que es necesario reducir el ruido que provoca.
     * @param input: recibe los valores en forma de cadena de decimales.
     * @param output: devuelve el resultado ya suavizado.
     * @param processNoise: este parámetro es útil para jugar entre rendimiento y precisión en la suavización de los datos.
     * @param measurementNoise:
     * @return
     */
    private float[] applyCombinedFilter(float[] input, float[] output, float processNoise, float measurementNoise) {
        // Si output es nulo, inicializamos un nuevo array con la misma longitud que input
        if (output == null) output = new float[input.length];

        // Inicialización de la covarianza del error y la varianza de la medición
        float errorCovariance = processNoise + measurementNoise;
        float measurementVariance = measurementNoise;

        // Recorrida de cada valor de la entrada
        for (int i = 0; i < input.length; i++) {
            // Aplicación de filtro paso bajo primero para suavizar los datos del sensor
            output[i] = output[i] + ALPHA * (input[i] - output[i]);

            // Cálculo de la ganancia de Kalman
            float kalmanGain = errorCovariance / (errorCovariance + measurementVariance);

            // Aplicación de filtro de Kalman para mejorar la precisión y reducir el ruido
            output[i] = output[i] + kalmanGain * (input[i] - output[i]);

            // Actualización de la covarianza del error para la próxima iteración
            errorCovariance = (1 - kalmanGain) * errorCovariance + processNoise;
        }
        return output; // Devuelve el array procesado con ambos filtros
    }
    // Fuente consultada:
    //[1] tiagoquin: https://github.com/AlexandreGabrielli/SYM-labo4/blob/d7bf4c92c2a033c5986284728822ab9317e6326d/README.md
    //[2] Bhide: Low-Pass-Filter-To-Android-Sensors - https://github.com/Bhide/Low-Pass-Filter-To-Android-Sensors
}

/**
 * Fuentes consultadas:
 *
 * [1] Android Tutorial Spot
 *      How to get accelerometer sensor data | Android Tutorial Spot
 *      https://www.youtube.com/watch?v=LsWJipo4knk
 *
 * [2]  Andy York
 *      Android Studio - Tutorial 6 - Accelerometer
 *      https://www.youtube.com/watch?v=YrI2pCZC8cc
 *
 * [3] Programmer World :
 *      How to read different sensors data in Android app? Demo using virtual sensors in Android 13 emulator
 *      https://www.youtube.com/watch?v=XD2L7vi6K_0
 *
 * [4] Philipp Lackner:
 *      How to Use Device Sensors the Right Way in Android - Android Studio Tutorial
 *      https://www.youtube.com/watch?v=IU-EAtITRRM
 *
 * [5] Programming w/ Professor Sluiter
 *      Android Studio Phone Accelerometer Shake Sensor and Line Graph
 *      https://www.youtube.com/watch?v=zUzZ67grYt8
 *
 * [6] Edward Lance Lorilla
 *      【Android Studio】 List of Device Sensors
 *      https://www.youtube.com/watch?v=k6mWFY8Z9hY
 *
 * [7] Edward Lance Lorilla
 *      【Android Studio】 Reading Sensor Data
 *      https://www.youtube.com/watch?v=f_JuQpZ-LJ4
 *
 * [8] Indently
 *      Accelerometer Sensor Tutorial in Android Studio (Kotlin 2021)
 *      https://www.youtube.com/watch?v=xcsuDDQHrLo&t=3s
 *
 * [9] Dr. Parag Shukla
 *      How to retrieve all hardware supported sensors - Android Kotlin
 *      https://www.youtube.com/watch?v=uAgF3ftb01w
 *
 * [10] Dr. Paul Muntean
 *      How to create a sensor data collecting app for Android
 *      https://www.youtube.com/watch?v=8mSwnk5RSA0
 *
 * [11] https://github.com/AlexandreGabrielli/SYM-labo4/blob/d7bf4c92c2a033c5986284728822ab9317e6326d/README.md
 *
 * [12] annasjovall: https://github.com/jhelbrink/MAMN01_MySensorApp/blob/765e305fef25221e988f93e63ba684f3ff87e319/MySensorApp/app/src/main/java/com/example/john/mysensorapp/Accelerometer.java
 *
 *
 */

