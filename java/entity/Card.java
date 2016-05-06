package entity;

public class Card {
	private String carNum;
	private Integer number;
	public String getCarNum() {
		return carNum;
	}
	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Card(String carNum, Integer number) {
		super();
		this.carNum = carNum;
		this.number = number;
	}
}
