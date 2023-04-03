package com.s8.arch.magnesium.stores.m3;

public class LoadList {

	public class Node {

		public int value;

		public Node previous;

		public Node next;

		public void move() {
			boolean isStandstill = false;
			while(!isStandstill) {
				// can move upstream
				if(previous!=null && previous.value < value) {
					Node swapped = previous, downstream = this.next;

					/**
					 * From:
					 * swaped.previous = upstream <-> swapped <-> this <-> this.next = downstream (MIGHT be null)
					 * To:
					 * upstream <-> this <-> swapped <-> downstream
					 * 
					 */
					if(swapped != head) { 
						Node upstream = swapped.previous;

						// double link 0
						upstream.next = this;
						this.previous = upstream;

					}
					/**
					 * From:
					 * head = swapped <-> this <-> this.next = downstream
					 * To:
					 * head = this <-> swapped <-> downstream
					 * 
					 */
					else {

						// double link 0
						head = this;
					}
					
					// double link 1
					this.next = swapped;
					swapped.previous = this;

					// double link 2
					swapped.next = downstream;
					if(downstream!=null) {
						downstream.previous = swapped;	
					}
				}
				// can move downstream
				else if(next != null && value < next.value){
					Node swapped = next, upstream = this.previous;
					
					/**
					 * From:
					 * this.previous = upstream <-> this <-> swapped <-> swapped.next = downstream
					 * To:
					 * upstream <-0-> swapped <-1-> this <-2-> downstream
					 * 
					 */
					if(swapped != tail) {
						Node downstream = swapped.next;
						
						// double link 2
						this.next = downstream;
						downstream.previous = this;
						
					}
					/**
					 * From:
					 * this.previous = upstream <-> this <-> swapped = tail
					 * To:
					 * upstream <-0-> swapped <-1-> this = tail
					 * 
					 */
					else {
						
						// double link 2
						tail = this;
					}
					
					// double link 0
					upstream.next = swapped;
					swapped.previous = upstream;
					
					// double link 1
					swapped.next = this;
					this.previous = swapped;
				}
				// cannot move at all
				else {
					isStandstill = true;
				}
			}
			
		}
	}


	private Node head;

	private Node tail;
	
	public void add() {
		
	}


}
