package com.s8.arch.magnesium.demos.user;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.magnesium.databases.user.MgUser;
import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.branch.BeOutbound;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytesIO;

public class MgUserbaseInit {

	public static void main(String[] args) throws BeBuildException, IOException {
		
		BeCodebase codebase = BeCodebase.from(MgUser.class);
		
		Path path = Path.of("data/user-db/userbase.be");
		
		BeBranch branch = new BeBranch(codebase);
		
		
		MgUser pierre = new MgUser("convert.pierre@gmail.com");
		pierre.displayName = "Pierre Convert";
		pierre.password = "toto1234";
		pierre.workspace = "convert.pierre@gmail.com-workspace";
		
		
		MgUser chris = new MgUser("christophe.convert@gmail.com");
		chris.displayName = "Christophe Convert";
		chris.password = "tata1234";
		chris.workspace = "christophe.convert@gmail.com-workspace";
		
		
		branch.set(pierre);
		branch.set(chris);
		
		
		
		
		
		/* build inflow */
		LinkedByteOutflow outflow = new LinkedByteOutflow();

		/* build outbound session */
		BeOutbound outbound = new BeOutbound(codebase);

		/* push branch */
		outbound.pushFrame(outflow, branch.pullDeltas());

		/* read from disk */
		LinkedBytesIO.write(outflow.getHead(), path, true);
	}

}
