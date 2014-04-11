package it.polimi.dima.watchdog.sms;

import java.util.HashMap;
import java.util.Map;

public class SMSValues {
	private Map<String,SMSValuesEnum> values = new HashMap<String,SMSValuesEnum>();
	
	public SMSValues(){
		this.values.put("SIREN_ON", SMSValuesEnum.SIREN_ON);
		this.values.put("SIREN_OFF", SMSValuesEnum.SIREN_OFF);
		this.values.put("MARK_LOST", SMSValuesEnum.MARK_LOST);
		this.values.put("MARK_STOLEN", SMSValuesEnum.MARK_STOLEN);
		this.values.put("MARK_LOST_OR_STOLEN", SMSValuesEnum.MARK_LOST_OR_STOLEN);
		this.values.put("MARK_FOUND", SMSValuesEnum.MARK_FOUND);
		this.values.put("LOCATE", SMSValuesEnum.LOCATE);
	}
	
	public Map<String,SMSValuesEnum> getValues(){
		return this.values;
	}
	
	
	
}
