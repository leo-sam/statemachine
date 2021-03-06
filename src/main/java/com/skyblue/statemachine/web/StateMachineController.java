package com.skyblue.statemachine.web;

import javax.annotation.Resource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyblue.statemachine.config.ComplexFormEvents;
import com.skyblue.statemachine.config.ComplexFormStateMachineBuilder;
import com.skyblue.statemachine.config.ComplexFormStates;
import com.skyblue.statemachine.config.Form;
import com.skyblue.statemachine.config.FormEvents;
import com.skyblue.statemachine.config.FormStateMachineBuilder;
import com.skyblue.statemachine.config.FormStates;
import com.skyblue.statemachine.config.InMemoryStateMachinePersist;
import com.skyblue.statemachine.config.MachineMap;
import com.skyblue.statemachine.config.Order;
import com.skyblue.statemachine.config.OrderEvents;
import com.skyblue.statemachine.config.OrderStateMachineBuilder;
import com.skyblue.statemachine.config.OrderStates;

@RestController
@RequestMapping("/statemachine")
public class StateMachineController {
	
	@Autowired
	private StateMachine orderSingleMachine;

	
	@Autowired
	private OrderStateMachineBuilder orderStateMachineBuilder;
	
	@Autowired
	private FormStateMachineBuilder formStateMachineBuilder;
	
	@Autowired
	private ComplexFormStateMachineBuilder complexFormStateMachineBuilder;
	
	@Autowired
	private InMemoryStateMachinePersist inMemoryStateMachinePersist;
	
	@Resource(name="orderMemoryPersister")
	private StateMachinePersister<OrderStates, OrderEvents, String> orderMemorypersister;
	
	@Resource(name="orderRedisPersister")
	private StateMachinePersister<OrderStates, OrderEvents, String> orderRedisPersister;
	
	@Resource(name="orderPersister")
	private StateMachinePersister<OrderStates, OrderEvents, Order> persister;

	@Autowired
	private BeanFactory beanFactory;
	
	@RequestMapping("/testSingleOrderState")
	public void testSingleOrderState() throws Exception {

		// ????????????
		orderSingleMachine.start();

		// ??????PAY??????
		orderSingleMachine.sendEvent(OrderEvents.PAY);

		// ??????RECEIVE??????
		orderSingleMachine.sendEvent(OrderEvents.RECEIVE);

//		Order order = new Order(orderId, "547568678", "??????????????????", "13435465465", "RECEIVE");
//		Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.RECEIVE).setHeader("order", order).build();
//		stateMachine.sendEvent(message);

		// ??????????????????
		System.out.println("???????????????" + orderSingleMachine.getState().getId());
	}
	
	
	@RequestMapping("/testOrderState")
	public void testOrderState(String orderId) throws Exception {

		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());

		// ????????????
		stateMachine.start();

		// ??????PAY??????
		stateMachine.sendEvent(OrderEvents.PAY);

		// ??????RECEIVE??????
		//stateMachine.sendEvent(OrderEvents.RECEIVE);
		
		//???message????????????
		Order order = new Order(orderId, "547568678", "??????????????????", "13435465465", "RECEIVE");
		Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.RECEIVE).setHeader("order", order).setHeader("otherObj", "otherObjValue").build();
		stateMachine.sendEvent(message);

		// ??????????????????
		System.out.println("???????????????" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testFormState")
	public void testFormState() throws Exception {

		StateMachine<FormStates, FormEvents> stateMachine = formStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());

		// ????????????
		stateMachine.start();

		stateMachine.sendEvent(FormEvents.WRITE);

		stateMachine.sendEvent(FormEvents.CONFIRM);

		stateMachine.sendEvent(FormEvents.SUBMIT);

		// ??????????????????
		System.out.println("???????????????" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testComplexFormState")
	public void testComplexFormState() throws Exception {

		StateMachine<ComplexFormStates, ComplexFormEvents> stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());
		
		Form form1 = new Form();
		form1.setId("111");
		form1.setFormName(null);
		
		Form form2 = new Form();
		form2.setId("222");
		form2.setFormName("????????????");
		
		Form form3 = new Form();
		form3.setId("333");
		form3.setFormName(null);

		// ????????????
		System.out.println("-------------------form1------------------");
		
		stateMachine.start();
		Message message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		
		System.out.println("-------------------form2------------------");
		stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		
		System.out.println("-------------------form3------------------");
		stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		form3.setFormName("????????????");
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
	}

	@RequestMapping("/testComplexFormStateResult")
	public void testComplexFormStateResult() throws Exception {

		StateMachine<ComplexFormStates, ComplexFormEvents> stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());

		Form form1 = new Form();
		form1.setId("111");
		form1.setFormName(null);

		Form form2 = new Form();
		form2.setId("222");
		form2.setFormName("????????????");

		Form form3 = new Form();
		form3.setId("333");
		form3.setFormName(null);

		// ????????????
		System.out.println("-------------------form1------------------");

		stateMachine.start();
		Message message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		Form result = (Form) message.getHeaders().get("form");
		System.out.println("?????????#########???" + result);

		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());

		System.out.println("-------------------form2------------------");
		stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());

		System.out.println("-------------------form3------------------");
		stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		form3.setFormName("????????????");
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("???????????????" + stateMachine.getState().getId());
	}
	@RequestMapping("/sendEvent")
    void sendEvent(String machineId,String events,String id) throws Exception{
		if(machineId.equals("form")) {
			StateMachine sm = MachineMap.formMap.get(id);
			Form form = new Form();
			form.setId(id);
			if(sm == null) {
				if(events.equals("WRITE")) {
					sm = formStateMachineBuilder.build(beanFactory);
					sm.start();
					MachineMap.formMap.put(id, sm);
				}else {
					System.out.println("???????????????????????????????????????"+events+"??????");
					return;
				}
			}
			
			Message message = MessageBuilder.withPayload(FormEvents.valueOf(events)).setHeader("form", form).build();
			sm.sendEvent(message);
		}
		if(machineId.equals("order")) {
			StateMachine sm = MachineMap.orderMap.get(id);
			Order order = new Order();
			order.setId(id);
			if(sm == null) {
				if(events.equals("PAY")) {
					sm = orderStateMachineBuilder.build(beanFactory);
					sm.start();
					MachineMap.orderMap.put(id, sm);
				}else {
					System.out.println("???????????????????????????????????????"+events+"??????");
					return;
				}
				
			}
			Message message = MessageBuilder.withPayload(OrderEvents.valueOf(events)).setHeader("order", order).setHeader("test","test1").build();
			sm.sendEvent(message);
		}
    }
	
	@RequestMapping("/testMemoryPersister")
	public void tesMemorytPersister(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		
		//??????PAY??????
		stateMachine.sendEvent(OrderEvents.PAY);
		Order order = new Order();
		order.setId(id);
		
		//?????????stateMachine
		orderMemorypersister.persist(stateMachine, order.getId());
	
	}
	
	@RequestMapping("/testMemoryPersisterRestore")
	public void testMemoryRestore(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		orderMemorypersister.restore(stateMachine, id);
		System.out.println("?????????????????????????????????" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testRedisPersister")
	public void testRedisPersister(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		Order order = new Order();
		order.setId(id);
		//??????PAY??????
		Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.PAY).setHeader("order", order).build();
		stateMachine.sendEvent(message);
		//?????????stateMachine
		orderRedisPersister.persist(stateMachine, order.getId());
	}
	
	@RequestMapping("/testRedisPersisterRestore")
	public void testRestore(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		orderRedisPersister.restore(stateMachine, id);
		System.out.println("?????????????????????????????????" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testOrderRestore")
	public void testOrderRestore(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		//??????
		Order order = new Order();
		order.setId(id);
		order.setState(OrderStates.WAITING_FOR_RECEIVE.toString());
		//??????
		persister.restore(stateMachine, order);
		//?????????????????????????????????
		System.out.println("?????????????????????" + stateMachine.getState().getId());
	}
}
