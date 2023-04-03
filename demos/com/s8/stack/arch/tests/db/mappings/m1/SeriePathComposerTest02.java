package com.s8.stack.arch.tests.db.mappings.m1;

import java.nio.file.Path;

import com.s8.arch.magnesium.stores.m1.modules.PathComposer;


public class SeriePathComposerTest02 {

	public static void main(String[] args) {
		PathComposer composer = new PathComposer(Path.of("data/"));
		
		System.out.println(composer.compose(0x00L, 0, "chunk",".ck").toString());
		System.out.println(composer.compose(0x01, 1, "chunk",".ck").toString());
		System.out.println(composer.compose(0x06, 3, "chunk",".ck").toString());
		System.out.println(composer.compose(0x7f, 8, "chunk",".ck").toString());
		System.out.println(composer.compose(0xff, 8, "chunk",".ck").toString());
		System.out.println(composer.compose(0x2f19, 16, "chunk",".ck").toString());
		System.out.println(composer.compose(0x222f19, 22, "chunk",".ck").toString());
		System.out.println(composer.compose(0x7f222f19, 31, "chunk",".ck").toString());
		System.out.println(composer.compose(0xaf222f19L, 32, "chunk",".ck").toString());
		System.out.println(composer.compose(0xaf222fffL, 32, "chunk",".ck").toString());
		
		System.out.println(composer.compose(0x7f22212f6f272fffL, 63, "chunk",".ck").toString());
		
	}

}
