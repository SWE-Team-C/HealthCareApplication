package edu.swe.healthcareapplication.util.step;

import java.util.Map;

public interface DataManager {

  void saveData(String key, Object value);

  Object getData(String key);

  Map<String, Object> getAllData();

}
