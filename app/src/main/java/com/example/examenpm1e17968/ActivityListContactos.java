package com.example.examenpm1e17968;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.examenpm1e17968.Tablas.Contactos;
import com.example.examenpm1e17968.Transacciones.Transacciones;

import java.util.ArrayList;



public class ActivityListContactos extends AppCompatActivity {

    SQLiteConexion conexion;
    ListView listacontactos;
    ArrayList<Contactos> listaC;
    ArrayList<String> arrayContactos;
    private String  idCont, telefono, nombre, pais, nota;

    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contactos);

        Button btnLlamar = (Button) findViewById(R.id.btnllamar);
        Button btncompartir = (Button) findViewById(R.id.btnCompartir);
        Button btnEditar = (Button) findViewById(R.id.btnmodificar);
        Button btnVolver = (Button) findViewById(R.id.btnregresar);
        EditText txtBuscar = (EditText) findViewById(R.id.txtbuscar);
        listacontactos = (ListView) findViewById(R.id.listcontactos);

        conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        listacontactos = (ListView) findViewById(R.id.listcontactos);


        ObtenerListaContactos();
        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayContactos);
        listacontactos.setAdapter(adp);


        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adp.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idCont != null){
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Confirmación de Llamada")
                            .setMessage("¿Desea llamar a " + nombre + "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(ActivityCompat.checkSelfPermission(ActivityListContactos.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                        Toast.makeText(getApplicationContext(), "Llamando en breve ", Toast.LENGTH_LONG).show();
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:+" + telefono));
                                        startActivity(callIntent);
                                    }else{
                                        ActivityCompat.requestPermissions(ActivityListContactos.this, new String[]{ Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }else{
                    mostrarDialogoSeleccion();
                }

            }
        });


        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(idCont != null){
                    Intent intent = new Intent(getApplicationContext(), ActivityEdit.class);
                    intent.putExtra("idCont", String.valueOf(idCont));
                    startActivity(intent);
                }else{
                    mostrarDialogoSeleccion();
                }
            }
        });

        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartirContacto();

            }
        });






        listacontactos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                idCont = listaC.get(position).getIdContacto().toString();
                nombre = listaC.get(position).getNombreContacto();
                pais = listaC.get(position).getPais().toString();

                telefono = obtenerCodigoMarcado(pais);

                telefono = telefono + listaC.get(position).getTelefonoContacto().toString();

                Toast.makeText(getApplicationContext(), "Ha seleccionado a: "+nombre, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void compartirContacto(){

        try {
            Intent compartir = new Intent(Intent.ACTION_SEND);
            compartir.setType("text/plain");
            compartir.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String aux = "Agregar Contacto \n";
            aux = aux + nombre + "+" + telefono ;
            compartir.putExtra(Intent.EXTRA_TEXT, aux);
            startActivity(compartir);

        }catch (Exception e){

        }

    }

    private void ObtenerListaContactos(){
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos listaContactos = null;
        listaC = new ArrayList<Contactos>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Transacciones.tablaContactos, null);

        while (cursor.moveToNext()){
            listaContactos = new Contactos();
            listaContactos.setIdContacto(cursor.getInt(0));
            listaContactos.setNombreContacto(cursor.getString(1));
            listaContactos.setTelefonoContacto(cursor.getInt(2));
            listaContactos.setPais(cursor.getInt(3));
            listaContactos.setNota(cursor.getString(4));

            listaC.add(listaContactos);
        }
        fillList();
    }

    private void fillList(){
        arrayContactos = new ArrayList<String>();

        SQLiteDatabase db = conexion.getReadableDatabase();
        for(int i = 0; i<listaC.size(); i++){

            String [] params = {listaC.get(i).getPais().toString()};
            String [] fields = {Transacciones.idPais,Transacciones.nombrePais};
            String whereCon = Transacciones.idPais + "=?";

            Cursor cData = db.query(Transacciones.tablaPaises, fields, whereCon, params, null, null, null, null);
            cData.moveToFirst();

            arrayContactos.add(cData.getString(1)+" // "+
                    listaC.get(i).getNombreContacto()+" // "+
                    listaC.get(i).getTelefonoContacto());
            cData.close();
        }
    }

    private void mostrarDialogoSeleccion() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Selección")
                .setMessage("Seleccione un contacto de la lista")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private String obtenerCodigoMarcado(String idPais) {
        String codigoMarcado;

        SQLiteDatabase db = conexion.getReadableDatabase();

        String [] params = {idPais}; //Parametro de Busqueda
        String [] fields = {Transacciones.idPais,Transacciones.codigoMarcado};
        String whereCon = Transacciones.idPais + "=?";

        Cursor cData = db.query(Transacciones.tablaPaises, fields, whereCon, params, null, null, null, null);
        cData.moveToFirst();

        codigoMarcado = String.valueOf(cData.getInt(1));

        cData.close();

        return codigoMarcado;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Llamando en breve ", Toast.LENGTH_LONG).show();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+" + telefono));
                    startActivity(callIntent);
                } else {

                }
                return;
            }
        }
    }
}