package com.harsh.demo.models;

import androidx.annotation.NonNull;

public class Meeting {
	public String clientName;
	public String companyName;

	@NonNull
	@Override
	public String toString() {
		return "Meeting{" +
				"clientName='" + clientName + '\'' +
				", companyName='" + companyName + '\'' +
				'}';
	}
}
