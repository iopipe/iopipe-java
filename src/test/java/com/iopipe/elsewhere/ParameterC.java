package com.iopipe.elsewhere;

import com.iopipe.IOpipeExecution;

public class ParameterC
	extends ParameterB<SimplePOJO>
{
	@Override
	public void normalCall(SimplePOJO __a)
	{
		IOpipeExecution.currentExecution().customMetric("parmc",
			(__a == null ? "null" : __a.getClass().getName()));
	}
}

