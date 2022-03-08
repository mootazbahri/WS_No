package com.almerys.generali.wsnoemisation.message.response;


import java.util.List;

import com.almerys.generali.wsnoemisation.model.Noemisation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {
	private String message;
	private List<Noemisation> noemisation;
}
