package com.s8.stack.arch.tests.db.mappings.m4;

import java.nio.file.Path;

import com.s8.stack.arch.magnesium.paths.NodePathComposer;

public class PathComposerTest01 {

	public static void main(String[] args) {
		NodePathComposer composer = new NodePathComposer(Path.of("data/"));
		for(long id=0; id<2048; id++) {
			System.out.println(composer.compose(id));
		}
	}

}
