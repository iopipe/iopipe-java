package com.iopipe.elsewhere;

import com.iopipe.IOpipeExecution;

public class ParameterA<A, B>
{
	public B handleRequest(A __a)
	{
		IOpipeExecution.currentExecution().label("squirrels");
		return null;
	}
}

