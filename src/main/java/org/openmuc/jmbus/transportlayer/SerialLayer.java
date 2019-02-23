/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.transportlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;


class SerialLayer implements TransportLayer {
    private final SerialBuilder<?,?> serialBuilder;
    private final int timeout;

    private DataOutputStream os;
    private DataInputStream is;
    private SerialPort serialPort;

    private CommPortIdentifier portIdentifier;

    public SerialLayer(int timeout, SerialBuilder<?,?> serialBuilder) {
        this.serialBuilder = serialBuilder;
        this.timeout = timeout;
    }

    @Override
    public void open() throws IOException {
    	  try {
              portIdentifier = CommPortIdentifier.getPortIdentifier(serialBuilder.getSerialPortName());
  			serialPort = (SerialPort) portIdentifier.open("jMBus", timeout);
              serialPort.setSerialPortParams(serialBuilder.getBaudrate(), serialBuilder.getDataBits(), serialBuilder.getStopBits(),
                     serialBuilder.getParity());
  	        os = new DataOutputStream(serialPort.getOutputStream());
  	        is = new DataInputStream(serialPort.getInputStream());
  		} catch (PortInUseException | NoSuchPortException | UnsupportedCommOperationException e) {
  			throw new IOException(e);
  }
        serialPort = (SerialPort) serialBuilder.build();
//        serialPort.setSerialPortTimeout(timeout);
        setTimeout(timeout);
        os = new DataOutputStream(serialPort.getOutputStream());
        is = new DataInputStream(serialPort.getInputStream());
    }

    @Override
    public DataOutputStream getOutputStream() {
        return os;
    }

    @Override
    public DataInputStream getInputStream() {
        return is;
    }

    @Override
    public void close() {
    	 if (serialPort == null) {
             return;
         }
    	 serialPort.close();
    }

    @Override
    public boolean isClosed() {
        return serialPort == null;
    }

    @Override
    public void setTimeout(int timeout) throws IOException {
        try {
			serialPort.enableReceiveTimeout(timeout);
		} catch (UnsupportedCommOperationException e) {
			throw new IOException(e);
		}
    }

    @Override
    public int getTimeout() {
        return serialPort.getReceiveTimeout();
    }

}
