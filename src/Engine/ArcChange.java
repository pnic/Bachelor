package Engine;

import java.util.Date;



public class ArcChange {

	private int old_first, new_first, old_second, new_second;
	private boolean firstChanged, secondChanged;
	private Date changedTime;
	
	public ArcChange(){
		old_first = -1;
		new_first = -1;
		old_second = -1;
		new_second = -1;
	}

	public Date getChangedTime() {
		return changedTime;
	}


	public void setChangedTime(Date changedTime) {
		this.changedTime = changedTime;
	}


	public int getOld_first() {
		return old_first;
	}

	public void setOld_first(int old_first) {
		this.old_first = old_first;
	}

	public int getNew_first() {
		return new_first;
	}

	public void setNew_first(int new_first) {
		this.new_first = new_first;
	}

	public int getOld_second() {
		return old_second;
	}

	public void setOld_second(int old_second) {
		this.old_second = old_second;
	}

	public int getNew_second() {
		return new_second;
	}

	public void setNew_second(int new_second) {
		this.new_second = new_second;
	}

	public boolean isFirstChanged() {
		return firstChanged;
	}

	public void setFirstChanged(boolean firstChanged) {
		this.firstChanged = firstChanged;
	}

	public boolean isSecondChanged() {
		return secondChanged;
	}

	public void setSecondChanged(boolean secondChanged) {
		this.secondChanged = secondChanged;
	}
	
	public String getChangedMessage(){
		String returner ="Changed ";
		if(firstChanged && secondChanged) {
			returner += "first number from " + old_first + " to " + new_first + " and second number from " + old_second + " to " + new_second;
			return returner;
		}
		if(firstChanged){
			returner += "first number from " + old_first + " to " + new_first;
		}
		if(secondChanged){
			returner += "second number from " + old_second + " to " + new_second;
		}
		return returner;
	}
}
