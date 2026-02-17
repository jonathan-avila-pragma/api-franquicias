// Script de Pre-request para Postman
// Coloca este código en la pestaña "Pre-request Script" de tu request en Postman

// Generar número aleatorio entre 1 y 1000
const randomNumber = Math.floor(Math.random() * 1000) + 1;

// Generar nombre con formato "fra" + número aleatorio
const franchiseName = `fra${randomNumber}`;

// Establecer la variable de entorno para usar en el body
pm.environment.set("franchise_name", franchiseName);

// También puedes establecer el número para logging
pm.environment.set("random_number", randomNumber);

// Log para debugging (opcional)
console.log(`Generated franchise name: ${franchiseName}`);
