package com.iopipe.elsewhere;

import com.iopipe.IOpipeExecution;

public class ParameterB<C extends SimplePOJO>
	extends ParameterA<C, Integer>
{
	@Override
	public void normalCall(C __a)
	{
		IOpipeExecution.currentExecution().customMetric("parmb",
			(__a == null ? "null" : __a.getClass().getName()));
	}
}

