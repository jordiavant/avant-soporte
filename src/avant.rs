// Avant Soporte: versión de RustDesk de Avant IT (fork de rustdesk/rustdesk, AGPL-3.0).
// Código fuente público: https://github.com/jordiavant/avant-soporte (rama `avant`).
//
// Deja la app configurada de fábrica con el servidor de Avant IT (ID, relay, API y clave pública
// del servidor) y sin comprobar actualizaciones: las versiones las instala el MDM de Avant. Los
// valores van como OVERWRITE_*, que mandan sobre lo que haya guardado el usuario. Se llama al
// arrancar la interfaz (flutter_ffi::initialize) y al arrancar solo el servicio (startServer, p. ej.
// al encender el equipo), que en Android van por caminos distintos.

use hbb_common::config;

/// Servidor de Avant IT (hbbs/hbbr en vpnavant, detrás de nginx para la API).
pub const SERVIDOR: &str = "remote.avant-it.net";
/// Clave pública del servidor (id_ed25519.pub). Es pública por diseño: los clientes la necesitan.
pub const CLAVE: &str = "mb10aCGhMlPe2Yavi5C5IwkxU42DGLNd76PV4bCLvU0=";
/// API de rustdesk-api (libreta de direcciones, lista de equipos), servida por nginx en el 443.
pub const API: &str = "https://remote.avant-it.net";
pub const NOMBRE: &str = "Avant Soporte";

pub fn sembrar() {
    // Con un nombre distinto de «RustDesk», la app se comporta como cliente personalizado: entre
    // otras cosas, no busca versiones nuevas en GitHub.
    *config::APP_NAME.write().unwrap() = NOMBRE.to_owned();

    let mut opciones = config::OVERWRITE_SETTINGS.write().unwrap();
    opciones.insert("custom-rendezvous-server".to_owned(), SERVIDOR.to_owned());
    opciones.insert("relay-server".to_owned(), SERVIDOR.to_owned());
    opciones.insert("api-server".to_owned(), API.to_owned());
    opciones.insert("key".to_owned(), CLAVE.to_owned());
    drop(opciones);

    let mut locales = config::OVERWRITE_LOCAL_SETTINGS.write().unwrap();
    locales.insert("enable-check-update".to_owned(), "N".to_owned());
}
