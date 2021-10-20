package com.company;

import java.io.*;
import java.util.*;

public class Universal_Hashing extends Open_Addressing{
	int a;
	int b;
	int p;

	protected Universal_Hashing(int w, int seed) {
		super(w, seed, -1);
		int temp = this.m+1; // m is even, so temp is odd here
		while(!isPrime(temp)) {
			temp += 2;
		}
		this.p = temp;
		a = generateRandom(0, p, seed);
		b = generateRandom(-1, p, seed);
	}
	
	/**
	 * Checks if the input int is prime
	 */
	public static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i*i <= n; i++) {
        	if (n % i == 0) return false;
        }
        return true;
    }
	
	/**
     * Implements universal hashing
     */
	@Override
    public int probe(int key, int i) {
		int h = ( (this.a*key + this.b) % this.p) % m;
		return ( (h+i) % power2(r) );
    }

    /**
     * Inserts key k into hash table. Returns the number of collisions encountered,
     * and resizes the hash table if needed
     */
	@Override
    public int insertKeyResize(int key) {

		float load_factor= (float) (size+1)/m; // we want to see if the load factor will be bigger on the next

		if (load_factor>MAX_LOAD_FACTOR) {
			int[] Table_copy=new int[m];
			for (int i=0;i<m;i++) {Table_copy[i]=this.Table[i];} // create a needed copy

			w=w+2;
			size=0;
			//A=generateRandom((int) power2(w - 1), (int) power2(w), seed);
			r = (int) (w - 1) / 2 + 1;
			m = power2(r);


			Table=new int[m];
			for (int i=0; i<m;i++) {Table[i]=-1;} // Fill the list with empty values
			for (int i=0;i<Table_copy.length;i++) {
				if (Table_copy[i]==-1) {} else {insertKey(Table_copy[i]);} // if empty, go next, if not, insert the key
			}
		}
		return insertKey(key);

    }
    @Override
	public int insertKey(int key) {
		int collisions=0;
		if (size!=m){
			for (int i=0;i<m;i++) {
				if (Table[probe(key, i)] != -1) { // if it isnt empty, then collision
					collisions++;
				}else {
					Table[probe(key,i)]=key; // if it is empty, no collision
					size++;
					break;
				}
			}
		}
		return collisions;
	}
}
