package com.forkexec.pts.ws;

import javax.jws.WebService;

import com.forkexec.pts.domain.Points;
import com.forkexec.pts.domain.PV;
import com.forkexec.pts.domain.exception.EmailAlreadyExistsFaultException;
import com.forkexec.pts.domain.exception.InvalidEmailFaultException;
import com.forkexec.pts.domain.exception.InvalidPointsFaultException;
import com.forkexec.pts.domain.exception.NotEnoughBalanceFaultException;
import com.forkexec.pts.ws.*;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(
		endpointInterface = "com.forkexec.pts.ws.PointsPortType", 
		wsdlLocation = "PointsService.wsdl", 
		name = "PointsWebService", 
		portName = "PointsPort", 
		targetNamespace = "http://ws.pts.forkexec.com/", 
		serviceName = "PointsService")
public class PointsPortImpl implements PointsPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private final PointsEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public PointsPortImpl(final PointsEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------

	@Override
	public void activateUser(final String userEmail)
			throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		try {
			final Points points = Points.getInstance();
			points.initAccount(userEmail);
		} catch (EmailAlreadyExistsFaultException e) {
			String message = e.getMessage();
			throwEmailAlreadyExistsFault(message);
		} catch (InvalidEmailFaultException e) {
			String message = e.getMessage();
			throwInvalidEmailFault(message);
		}
	}

	@Override
	public PointsView getPoints(final String userEmail) throws InvalidEmailFault_Exception {
		try {
			final Points points = Points.getInstance();
			return pointsViewBuilder(points.getPoints(userEmail));
		} catch (InvalidEmailFaultException e) {
			throwInvalidEmailFault(e.getMessage());
			return null;
		}
	}

	@Override
	public int setPoints(final String userEmail, final int pointsToAdd, final int tag)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		try {
			final Points points = Points.getInstance();
			points.setPoints(userEmail, pointsToAdd, tag);
			return points.getAccountPoints(userEmail).getPoints();
		} catch (InvalidEmailFaultException e) {
			throwInvalidEmailFault(e.getMessage());
		} catch (InvalidPointsFaultException e) {
			throwInvalidPointsFault(e.getMessage());
		}
		return -1;
	}


	// Control operations ----------------------------------------------------
	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the service does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = PointsApp.class.getSimpleName();

		// Build a string with a message to return.
		final StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		Points.getInstance().reset();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInit(final int startPoints) throws BadInitFault_Exception {
		Points.getInstance().setInitialBalance(startPoints);
	}

	public PointsView pointsViewBuilder(PV pv){
		PointsView pointsV = new PointsView();
		pointsV.setPoints(pv.getPoints());
		pointsV.setTag(pv.getTag());
		return pointsV;
	}

	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInitFault_Exception {
		final BadInitFault faultInfo = new BadInitFault();
		faultInfo.message = message;
		throw new BadInitFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new EmailAlreadyExistsFault exception. */
	private void throwEmailAlreadyExistsFault(final String message) throws EmailAlreadyExistsFault_Exception {
		final EmailAlreadyExistsFault faultInfo = new EmailAlreadyExistsFault();
		faultInfo.message = message;
		throw new EmailAlreadyExistsFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new InvalidEmailFault exception. */
	private void throwInvalidEmailFault(final String message) throws InvalidEmailFault_Exception {
		final InvalidEmailFault faultInfo = new InvalidEmailFault();
		faultInfo.message = message;
		throw new InvalidEmailFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new NotEnoughBalanceFault exception. */
	private void throwNotEnoughBalanceFault(final String message) throws NotEnoughBalanceFault_Exception {
		final NotEnoughBalanceFault faultInfo = new NotEnoughBalanceFault();
		faultInfo.message = message;
		throw new NotEnoughBalanceFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new InvalidPointsFault exception. */
	private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
		final InvalidPointsFault faultInfo = new InvalidPointsFault();
		faultInfo.message = message;
		throw new InvalidPointsFault_Exception(message, faultInfo);
	}

}
