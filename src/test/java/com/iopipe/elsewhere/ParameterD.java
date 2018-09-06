package com.iopipe.elsewhere;

import com.iopipe.IOpipeExecution;

public class ParameterD
	extends ParameterA<SimplePOJO, Float>
{
	@Override
	public void normalCall(SimplePOJO __a)
	{
		IOpipeExecution.currentExecution().customMetric("parmd",
			(__a == null ? "null" : __a.getClass().getName()));
	}
}

