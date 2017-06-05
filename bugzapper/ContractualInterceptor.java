package bugzapper;

import javax.interceptor.Interceptor;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.ScriptEngineManager;

// NOTE: Adding a priority means that this interceptor will be automatically registered
import javax.annotation.Priority;
import static javax.interceptor.Interceptor.Priority.APPLICATION;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

@Contractual @Interceptor @Priority(APPLICATION)
public class ContractualInterceptor {
	ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	ScriptEngine javascriptEngine = scriptEngineManager.getEngineByName("nashorn");

	{
		try {
			javascriptEngine.eval("var Parameters = Java.type('parcheesi.Parameters')");
		} catch (ScriptException e) {
			System.out.println("Contract ScriptException occurred -- Parameters global may not exist.");
		}
	}

	@AroundInvoke
	public Object doTheThing(InvocationContext ctx) throws Exception {
		System.out.println("Doing the thing!"
				+ "\n\tto: " + ctx.getTarget().getClass().getName().split("\\$")[0]
					+ "{{hashcode:" + ctx.getTarget().hashCode() + "}}"
				+ "\n\twhile it tries to: " + ctx.getMethod().getName()
		);

		javascriptEngine.put("self", ctx.getTarget());

		List<String> parameterNames = Arrays.asList(ctx.getMethod().getParameters())
			.stream()
			.map(p -> p.getName())
			.collect(java.util.stream.Collectors.toList());
		
		Precondition precondition;
		Postcondition postcondition;

		String preconditionCode = "true";
		String postconditionCode = "true";

		try {
			precondition = ctx.getMethod().getAnnotation(Precondition.class);
			preconditionCode = precondition.value();
		} catch (NullPointerException n) {
			// No precondition
		}

		try {
			postcondition = ctx.getMethod().getAnnotation(Postcondition.class);
			postconditionCode = postcondition.value();
		} catch (NullPointerException n) {
			// No postcondition
		}

		Invocable invocableEngine = (Invocable) javascriptEngine;

		// precondition
		if (preconditionCode.equals("true")) {
			System.out.println("Skipping empty precondition");
		} else {
			javascriptEngine.eval(
				"function Precondition (" + String.join(", ", parameterNames) + ") { "
					+ "return " + preconditionCode
				+ "}"
			);

			System.out.println("Evalutating precondition: " + preconditionCode);
			Object preconditionResult = invocableEngine.invokeFunction("Precondition", ctx.getParameters());
			System.out.println(preconditionResult);
			if (!Boolean.valueOf(preconditionResult.toString())) {
				throw new RuntimeException("Contract broken!");
			}
		}

		Object result = ctx.proceed();

		// postcondition
		if (postconditionCode.equals("true")) {
			System.out.println("Skipping empty postcondition");
		} else {
			parameterNames.add("result");

			javascriptEngine.eval(
				"function Postcondition (" + String.join(", ", parameterNames) + ") {"
					+ "return " + postconditionCode
				+ "}"
			);

			System.out.println("Evalutating postcondition: " + postconditionCode);
			ArrayList<Object> args = new ArrayList(Arrays.asList(ctx.getParameters()));
			args.add(result);
			Object postconditionResult = invocableEngine.invokeFunction("Postcondition", args.toArray());
			System.out.println(postconditionResult);
			if (!Boolean.valueOf(postconditionResult.toString())) {
				throw new RuntimeException("Contract broken!");
			}
		}

		return result;
	}
}
