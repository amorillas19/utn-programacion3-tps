const palabras = ["casa", "ana", "sol", "javascript", "asa"];

console.log("Palabras:", palabras);

const quiereComprobar = confirm("¿Quieres comprobar cuáles son palíndromos?");

if (quiereComprobar) {
  console.log("\nResultados:\n");
  
  const palindromos = [];

  for (let palabra of palabras) {
    const invertida = palabra.split('').reverse().join('');
    const esPalindromo = palabra.toLowerCase() === invertida.toLowerCase();

    if (esPalindromo) {
      palindromos.push(palabra);
      console.log(palabra + " -> " + invertida + " : SÍ");
    } else {
      console.log(palabra + " -> " + invertida + " : NO");
    }
  }

  console.log("\nTotal palindromos encontrados: " + palindromos.length);
  
  if (palindromos.length > 0) {
    alert("Palíndromos encontrados:\n" + palindromos.join("\n"));
  } else {
    alert("No hay palíndromos en el array.");
  }

} else {
  console.log("El usuario canceló la comprobación.");
}
