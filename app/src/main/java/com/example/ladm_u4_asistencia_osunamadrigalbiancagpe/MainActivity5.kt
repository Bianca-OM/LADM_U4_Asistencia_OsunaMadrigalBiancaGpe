package com.example.ladm_u4_asistencia_osunamadrigalbiancagpe

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ladm_u4_asistencia_osunamadrigalbiancagpe.databinding.ActivityMain5Binding
import com.google.android.material.snackbar.Snackbar

class MainActivity5 : AppCompatActivity(), DevicesRecyclerViewAdapter.ItemClickListener,
    ChatFragment.CommunicationListener {
    lateinit var binding: ActivityMain5Binding

    private val REQUEST_ENABLE_BT = 123
    private val REQUEST_ENABLE_DISCO_BT = 456
    private val REQUEST_ENABLE_SEARCH_BT = 789

    private val PERMISSION_REQUEST_LOCATION = 123
    private val PERMISSION_REQUEST_LOCATION_KEY = "PERMISSION_REQUEST_LOCATION"

    private var alreadyAskedForPermission = false
    private var connected: Boolean = false
    private var mBtAdapter: BluetoothAdapter? = null
    private var mChatService: BluetoothChatService? = null
    private val mDeviceList = arrayListOf<DeviceData>()
    private lateinit var devicesAdapter: DevicesRecyclerViewAdapter
    private lateinit var  mConnectedDeviceName: String
    private lateinit var chatFragment: ChatFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lblStatus.text = getString(R.string.bluetooth_not_enabled)

        if (savedInstanceState != null)
            alreadyAskedForPermission = savedInstanceState.getBoolean(PERMISSION_REQUEST_LOCATION_KEY, false)

        binding.recyclerViewFound.layoutManager = LinearLayoutManager(this)

        binding.btnBuscar.setOnClickListener {
            if(mBtAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_DISCO_BT)
            }else if (mBtAdapter?.isDiscovering ?: false) {
                AlertDialog.Builder(this)
                    .setMessage("Búsqueda en progreso, ¿Desea detenerla?")
                    .setNegativeButton("DETENER",{d,i->
                        startDiscovery()
                        d.dismiss()
                    })
                    .setNeutralButton("CANCELAR",{d,i->d.cancel()})
                    .show()
            }else{
                makeVisible()
            }
        }

        devicesAdapter = DevicesRecyclerViewAdapter(context = this, mDeviceList = mDeviceList)
        binding.recyclerViewFound.adapter = devicesAdapter
        devicesAdapter.setItemClickListener(this)

        // Receiver detecta cuando se encuentre un dispositivo
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        // Receiver detecta cuando se terminó la búsqueda
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mReceiver, filter)

        // Obtener el adaptador Bluetooth local
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()

        // Iniciar el servicio de chat bluetooth para realizar conexiones
        mChatService = BluetoothChatService(this, mHandler)

        if (mBtAdapter == null)
            showAlertAndExit()
        else {
            if (mBtAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                binding.lblStatus.text = getString(R.string.bluetooth_on)
                binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
            }
        }
    }

    fun makeVisible() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(discoverableIntent, REQUEST_ENABLE_SEARCH_BT)
    }

    fun showAlertAndExit() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.not_compatible))
            .setMessage(getString(R.string.no_support))
            .setPositiveButton("Exit", { _, _ -> System.exit(0) })
            .show()
    }

    fun findDevices() {
        if (alreadyAskedForPermission) {
            // No vuelve a comprobar los permisos
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Comprobar permisos en Manifest
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.need_loc_access))
                builder.setMessage(getString(R.string.please_grant_loc_access))
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    // Ya está comprobando permisos
                    alreadyAskedForPermission = true
                    requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_REQUEST_LOCATION)
                }
                builder.show()
            } else {
                startDiscovery()
            }
        } else {
            startDiscovery()
            alreadyAskedForPermission = true
        }
    }

    fun startDiscovery() {
        mDeviceList.clear()
        // Si ya está en proceso se detiene
        if (mBtAdapter?.isDiscovering ?: false) {
            mBtAdapter?.cancelDiscovery()
            AlertDialog.Builder(this).setMessage("DETENIDO").show()
        }else {
            // Comenzar a buscar dispositivos disponibles
            binding.lblBuscando.visibility = View.VISIBLE
            binding.progressBuscando.visibility = View.VISIBLE
            mBtAdapter?.startDiscovery()
        }
    }

    private val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                // Obtener la información del dispositivo bluetooth encontrado
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device?.name
                val deviceHardwareAddress = device?.address // MAC address

                val deviceData = DeviceData(deviceName, deviceHardwareAddress!!)
                mDeviceList.add(deviceData)

                val setList = HashSet<DeviceData>(mDeviceList)
                mDeviceList.clear()
                mDeviceList.addAll(setList)
                devicesAdapter.notifyDataSetChanged()
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                binding.lblBuscando.visibility = View.GONE
                binding.progressBuscando.visibility = View.GONE
            }
        }
    }

    override fun itemClicked(deviceData: DeviceData) {
        connectDevice(deviceData)
    }

    private fun connectDevice(deviceData: DeviceData) {
        // Cancelar búsqueda
        mBtAdapter?.cancelDiscovery()
        binding.lblBuscando.visibility = View.GONE
        binding.progressBuscando.visibility = View.GONE

        val deviceAddress = deviceData.deviceHardwareAddress
        val device = mBtAdapter?.getRemoteDevice(deviceAddress)

        // Intentar conectarse al dispositivo
        mChatService?.connect(device, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //Bluetooth está habilitado
            binding.lblStatus.text = getString(R.string.bluetooth_on)
            binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
        }
        if (requestCode == REQUEST_ENABLE_DISCO_BT && resultCode == 0) {
            //Bluetooth está deshabilitado
            binding.lblStatus.text = getString(R.string.bluetooth_not_enabled)
            binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_red))
        }
        if (requestCode == REQUEST_ENABLE_DISCO_BT && resultCode == Activity.RESULT_OK) {
            //Bluetooth está habilitado
            binding.lblStatus.text = getString(R.string.bluetooth_on)
            binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
            makeVisible()
        }
        if (requestCode == REQUEST_ENABLE_SEARCH_BT && resultCode > 0) {
            findDevices()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                // Ya no está pidiendo permiso
                alreadyAskedForPermission = false
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.fun_limted))
                        builder.setMessage(getString(R.string.since_perm_not_granted))
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.show()
                    }
                }
            }
        }
    }

    /**
     * Handler para obtener la información de BluetoothChatService
     */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: android.os.Message) {

            when (msg.what) {

                Constants.MESSAGE_STATE_CHANGE -> {

                    when (msg.arg1) {
                        BluetoothChatService.STATE_CONNECTED -> {
                            binding.lblStatus.text = getString(R.string.connected_to) + " "+ mConnectedDeviceName
                            binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
                            Snackbar.make(findViewById(R.id.lyMain),"Conectado a ${mConnectedDeviceName}",Snackbar.LENGTH_SHORT).show()
                            connected = true
                        }
                        BluetoothChatService.STATE_CONNECTING -> {
                            binding.lblStatus.text = getString(R.string.connecting)
                            binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connecting))
                            connected = false
                        }
                        BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                            binding.lblStatus.text = getString(R.string.not_connected)
                            binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_red))
                            Snackbar.make(findViewById(R.id.lyMain),getString(R.string.not_connected),Snackbar.LENGTH_SHORT).show()
                            connected = false
                        }
                    }
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    val milliSecondsTime = System.currentTimeMillis()
                    chatFragment.communicate(Message(writeMessage,milliSecondsTime,Constants.MESSAGE_TYPE_SENT))
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    val milliSecondsTime = System.currentTimeMillis()
                    chatFragment.communicate(Message(readMessage,milliSecondsTime,Constants.MESSAGE_TYPE_RECEIVED))
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)!!
                    binding.lblStatus.text = getString(R.string.connected_to) + " " +mConnectedDeviceName
                    binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
                    Snackbar.make(findViewById(R.id.lyMain),"Connected to " + mConnectedDeviceName,Snackbar.LENGTH_SHORT).show()
                    connected = true
                    //showChatFragment()
                }
                Constants.MESSAGE_TOAST -> {
                    binding.lblStatus.text = getString(R.string.not_connected)
                    binding.imgConnectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_red))
                    Snackbar.make(findViewById(R.id.lyMain),
                        msg.data.getString(Constants.TOAST)!!,Snackbar.LENGTH_SHORT).show()
                    connected = false
                }
            }
        }
    }

    fun sendMessage(message: String) {

        // Check that we're actually connected before trying anything
        if (mChatService?.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return
        }

        // Check that there's actually something to send
        if (message.isNotEmpty()) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mChatService?.write(send)
        }
    }

    fun showChatFragment() {
        if(!isFinishing) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            chatFragment = ChatFragment.newInstance()
            chatFragment.setCommunicationListener(this)
            fragmentTransaction.replace(R.id.lyMain, chatFragment, "ChatFragment")
            fragmentTransaction.addToBackStack("ChatFragment")
            fragmentTransaction.commit()
        }
    }

    override fun onCommunication(message: String) {
        sendMessage(message)
    }
}