const palabras = ["casa", "sol", "mesa", "programación", "javascript"];

// 1. Longitud de cada palabra
console.log("Longitud de cada palabra:");
for (let i = 0; i < palabras.length; i++) {
  console.log(`${palabras[i]}: ${palabras[i].length} caracteres`);
}

console.log("\n");

// 2. Alternativa con loop 
let masLarga = palabras[0];
for (let i = 1; i < palabras.length; i++) {
  if (palabras[i].length > masLarga.length) {
    masLarga = palabras[i];
  }
}
console.log(`Palabra mas larga: "${masLarga}"`);

console.log("\n");

// 3. Palabras que contienen "a"
console.log("\nDetalle:");
for (let palabra of palabras) {
  if (palabra.includes("a")) {
    console.log(`✓ ${palabra}`);
  }
}
