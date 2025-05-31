/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.utils;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

@Slf4j
public class FileUtils
{
	public static void createDirectories(File... files)
	{
		for (File f : files)
		{
			if (f.exists())
			{
				continue;
			}

			if (f.mkdirs())
			{
				log.info("Created directory: {}", f.getAbsolutePath());
			}
			else
			{
				log.error("Failed to create directory: {}", f.getAbsolutePath());
				throw new RuntimeException("Failed to create directory: " + f.getAbsolutePath());
			}
		}
	}

	/**
	 * Get current executing jar file
	 *
	 * @return Jar file
	 */
	@Nullable
	public static File getJarFile()
	{
		try
		{
			return new File(URLDecoder.decode(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(), StandardCharsets.UTF_8.name()));
		}
		catch (Exception e)
		{
			log.error("Failed to get jar file", e);
		}
		return null;
	}
}