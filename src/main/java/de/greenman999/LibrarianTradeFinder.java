package de.greenman999;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibrarianTradeFinder implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("librarian-trade-finder");

	@Override
	public void onInitialize() {
		LOGGER.info("Librarian Trade Finder initialized.");
	}
}