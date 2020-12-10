package com.harsh.demo;

import androidx.annotation.NonNull;

public class Meeting {
	String clientName;
	String companyName;

	@NonNull
	@Override
	public String toString() {
		return "Meeting{" +
				"clientName='" + clientName + '\'' +
				", companyName='" + companyName + '\'' +
				'}';
	}
}
