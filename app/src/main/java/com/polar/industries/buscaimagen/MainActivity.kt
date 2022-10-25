package com.polar.industries.buscaimagen

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_busca_pokemon.*
import kotlinx.android.synthetic.main.dialog_busca_pokemon.view.*
import kotlinx.coroutines.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var pokedex: List<String> = listOf(
        "Absol", "Alakazam", "Ampharos", "Arcanine", "Aron", "Blastoise", "Buizel", "Bulbasaur", "Charizard", "Charmander", "Chicorita", "Cyndaquil", "Dragonite", "Electabuzz",
        "Gengar", "Golduck", "Growlithe", "Heracross", "Jolteon", "Kangaskhan", "Lapras", "Lucario", "Ludicolo", "Lugia", "Mankey", "Mewtwo", "Mudkip", "Ninetales", "Phanpy", "Pikachu",
        "Piplup", "Primeape", "Psyduck", "Rapidash", "Rhyhorn", "Seel", "Snorlax", "Squirtle", "Torkoal", "Totodile"
    )

    private val URIPLACEHOLDER: String = "@drawable/placeholder"

    private var hiloImagenUno: Job? = null
    private var hiloImagenDos: Job? = null
    private var hiloImagenTres: Job? = null
    private var banderaEncontrado: Boolean = false
    private var pokeABuscar: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        floatingActionButtonShowDialog.setOnClickListener {
            resetear()
            showDialogBusqueda()
        }
    }

    private fun iniciarBusqueda(pokeBusqueda: String){
        pokeABuscar = pokeBusqueda
        hiloImagenUno = CoroutineScope(Dispatchers.Main).launch{
            iniciarProceso(imageViewBuscaUno, imageViewGanaUno)
        }

        hiloImagenDos = CoroutineScope(Dispatchers.Main).launch{
            iniciarProceso(imageViewBuscaDos, imageViewGanaDos)
        }

        hiloImagenTres = CoroutineScope(Dispatchers.Main).launch{
            iniciarProceso(imageViewBuscaTres, imageViewGanaTres)
        }
    }

    private suspend fun iniciarProceso(imageView: ImageView, imageViewGanador: ImageView){
        val imageResource = resources.getIdentifier(URIPLACEHOLDER, null, packageName)
        imageView.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, imageResource))

        while (!pokeABuscar.equals(imageView.contentDescription) && !banderaEncontrado){
            delay(10)
            var aux: String = pokedex[(0..pokedex.size).random()].toString().toLowerCase()
            var uriAleatorio: String = "@drawable/$aux"
            val imageResource = resources.getIdentifier(uriAleatorio, null, packageName)
            imageView.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, imageResource))

            imageView.contentDescription = aux
        }

        if(!banderaEncontrado){
            banderaEncontrado = true
            imageViewGanador.visibility = View.VISIBLE
            Toast.makeText(this@MainActivity, "Se encontró al pokémon", Toast.LENGTH_LONG).show()
        }

    }


    private fun showDialogBusqueda(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val view: View = layoutInflater.inflate(R.layout.dialog_busca_pokemon, null)
        builder.setView(view)

        val dialog: Dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()


        val adapter = ArrayAdapter(this@MainActivity, R.layout.custom_spinner_item, pokedex)
        (view.textInputLayoutPokemon.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        view.buttonGuardar.setOnClickListener {
            var seleccion: String = view.textInputLayoutPokemon.editText?.text.toString().toLowerCase()

            Toast.makeText(this@MainActivity, seleccion, Toast.LENGTH_LONG).show()
            iniciarBusqueda(seleccion)
            dialog.dismiss()
        }
    }



    fun ClosedRange<Int>.random() =
        Random().nextInt(endInclusive-start)+start

    private fun resetear(){
        banderaEncontrado = false
        hiloImagenUno?.cancel()
        hiloImagenDos?.cancel()
        hiloImagenTres?.cancel()
        pokeABuscar = ""
        imageViewGanaUno.visibility = View.INVISIBLE
        imageViewGanaDos.visibility = View.INVISIBLE
        imageViewGanaTres.visibility = View.INVISIBLE
        imageViewBuscaUno.contentDescription = ""
        imageViewBuscaDos.contentDescription = ""
        imageViewBuscaTres.contentDescription = ""
    }
}