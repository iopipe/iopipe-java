package com.iopipe.elsewhere;

import com.iopipe.IOpipeExecution;

public class ParameterA<A extends SomeType, B>
{
	public void normalCall(A __a)
	{
		IOpipeExecution.currentExecution().customMetric("parma",
			(__a == null ? "null" : __a.getClass().getName()));
	}
	
	public B handleRequest(A __a)
	{
		IOpipeExecution.currentExecution().label("parameter-" +
			this.getClass().getName() + "-" +
			(__a == null ? "null" : __a.getClass().getName()));
		IOpipeExecution.currentExecution().customMetric("class",
			(__a == null ? "null" : __a.getClass().getName()));
		
		this.normalCall(__a);
		
		return null;
	}
}

