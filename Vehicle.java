import java.util.UUID;

public class Vehicle {

  private String vehicleNo;
   boolean run;
  /**
   * Varsayılan kurucu fonksiyon
   */
   Vehicle() {
      this.vehicleNo = UUID.randomUUID().toString();
      /* Başlangıçta false */
       run = false;
  }
  /**
   *
   * @param vehicleNo UUID olarak motor id
   * @return motor instance
   */
  public Vehicle(String vehicleNo) {
      /*
       * Varolan bir motor no ile oluşturuluyor.
       */
      this.vehicleNo = vehicleNo;
      run = false; // false yapılıyor
  }
  public void run() {
/**
* calisiyor true yapılıyor
*/
      run = true;
  }
  /**
   * Motorun durdurulması //
   */
  public void stop() {
      run = false;
  }
  public String getVehicleNo() {
// motor no getir
      return vehicleNo;
  }
  @Override
  public String toString() {
// durum belirlenmesi //
      String durum = run ? "Motor Çalışıyor." : "Motor Çalışmıyor";
      return durum;
  }
}