package entity;

import java.util.Date;

public class Student {
	private Integer studyNum;
	private String name;
	private Date beginDate;
	private Card card;
	public Integer getStudyNum() {
		return studyNum;
	}
	public void setStudyNum(Integer studyNum) {
		this.studyNum = studyNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public Student(Integer studyNum, String name, Date beginDate, Card card) {
		super();
		this.studyNum = studyNum;
		this.name = name;
		this.beginDate = beginDate;
		this.card = card;
	}

}
