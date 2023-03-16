package com.skymapglobal.vnstock.utils;

public class Tuple {
  public static class T4<Q,W,E,R> {
    private Q first;
    private W second;
    private E third;
    private R four;

    public T4(Q first, W second, E third, R four) {
      this.first = first;
      this.second = second;
      this.third = third;
      this.four = four;
    }

    public Q getFirst() {
      return first;
    }

    public W getSecond() {
      return second;
    }

    public E getThird() {
      return third;
    }

    public R getFour() {
      return four;
    }
  }
}
