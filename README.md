Esta aplicación desarrollada en Android con Jetpack Compose permite explorar una lista de ciudades, realizar búsquedas en tiempo real, marcar ciudades como favoritas, visualizar su ubicación en un mapa y acceder a información adicional mediante la API de Wikipedia.

Enfoque técnico y decisiones de diseño
Manejo de estado y persistencia
Para garantizar una experiencia fluida y reactiva, se optó por el uso de StateFlow en conjunto con DataStore:

StateFlow permite una gestión asincrónica del estado de la interfaz de usuario, respondiendo de forma inmediata ante cualquier cambio en los datos.

DataStore se utiliza para almacenar de manera persistente los identificadores de las ciudades marcadas como favoritas, asegurando que esta información se mantenga disponible incluso entre instalaciones de la aplicación.

Este enfoque facilita una interfaz altamente responsiva, con actualizaciones automáticas en la visualización de datos sin necesidad de operaciones manuales de recarga.

Filtrado y ordenamiento
En el ViewModel principal se emplea la función combine, que permite observar de forma simultánea múltiples flujos de datos: la lista de ciudades, el texto ingresado en la búsqueda, los favoritos y la preferencia de visualización de solo favoritos.

Ante cualquier modificación en estas fuentes, se ejecuta una función que:

- Filtra las ciudades según el texto de búsqueda.

- Aplica el filtro de favoritos si corresponde.

- Ordena alfabéticamente los resultados por nombre y país.

Esto garantiza un filtrado eficiente y preciso, adaptado a los criterios establecidos por el usuario.

Visualización en mapa
Para la representación geográfica de las ciudades seleccionadas se optó por un framework de mapas de código abierto. Esta elección se basó en la necesidad de evitar dependencias con servicios que requieren configuración de credenciales y métodos de pago, como es el caso de Google Maps.

El framework seleccionado permite renderizar correctamente las coordenadas geográficas de las ciudades sin requerimientos adicionales, y ofrece una experiencia satisfactoria dentro del contexto del proyecto.

Información adicional desde Wikipedia
Se integró la API pública de Wikipedia con el fin de brindar al usuario un resumen informativo sobre cada ciudad. Esta funcionalidad se encuentra disponible en la pantalla de detalle de ciudad.

Durante su implementación se presentaron desafíos relacionados con ciudades homónimas (mismo nombre, distinto país). Para resolverlo, se incorporó lógica adicional que mejora la precisión al consultar Wikipedia, considerando tanto el nombre como el país cuando es posible.

Adicionalmente, se habilitó la posibilidad de que el usuario acceda directamente a:

- La entrada de la ciudad en Wikipedia.

- Una búsqueda de la ciudad en Google.

Arquitectura y navegación
La aplicación adopta una arquitectura basada en una única Activity, utilizando Navigation-Compose como mecanismo de navegación entre pantallas. Esta elección proporciona mayor flexibilidad y desacoplamiento entre componentes.

El ViewModel principal (CitiesViewModel) es compartido por múltiples pantallas, lo que permite:

- Mantener un estado centralizado y consistente.

- Facilitar la reutilización de lógica.

- Reducir complejidad innecesaria en la navegación o la lógica de presentación.

